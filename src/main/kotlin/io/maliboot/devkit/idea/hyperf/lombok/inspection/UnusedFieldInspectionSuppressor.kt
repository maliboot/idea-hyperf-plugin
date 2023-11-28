package io.maliboot.devkit.idea.hyperf.lombok.inspection

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.inspections.PhpUnusedPrivateFieldInspection
import com.jetbrains.php.lang.psi.elements.Field
import io.maliboot.devkit.idea.hyperf.lombok.psi.FakePsiPhpMethod
import io.maliboot.devkit.idea.hyperf.lombok.psi.PhpClassEx


class UnusedFieldInspectionSuppressor {
    companion object {
        private fun isLombokField(element: PsiElement?): Boolean {
            if (element !is Field || element.containingClass == null) {
                return false
            }
            return ReadAction.compute<Boolean, RuntimeException> {
                return@compute PhpClassEx(element.containingClass!!).getFakeMethods().any {
                    (it.value as FakePsiPhpMethod).generatedByFQN == element.fqn
                }
            }
        }
    }

    internal class UnusedPrivateFieldInspectionSuppressor : InspectionSuppressor {
        override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
            return if (toolId != PhpUnusedPrivateFieldInspection().id) {
                false
            } else isLombokField(element.parent)
        }

        override fun getSuppressActions(element: PsiElement?, toolId: String): Array<out SuppressQuickFix> {
            return SuppressQuickFix.EMPTY_ARRAY
        }
    }
}