package io.maliboot.www.hyperf.extension.aop

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.inspections.PhpMultipleClassDeclarationsInspection
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import io.maliboot.www.hyperf.lombok.extend.getPhpClasses


class MultipleClassDeclarationsInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (toolId != PhpMultipleClassDeclarationsInspection().id) {
            return false
        }

        val parentElement = element.parent
        if (parentElement is ClassReference) {
            return proxyExisted(parentElement.project, parentElement.fqn)
        }

        return if (parentElement is PhpClass) {
            proxyExisted(parentElement.getProject(), parentElement.fqn)
        } else false
    }

    private fun proxyExisted(project: Project, classFQN: String?): Boolean {
        return ReadAction.compute<Boolean, RuntimeException> {
            if (classFQN == null) {
                return@compute false
            }
            return@compute classFQN.getPhpClasses(project).any {
                it.containingFile.name.contains("proxy.php")
            }
        }
    }

    override fun getSuppressActions(
        element: PsiElement?,
        toolId: String
    ): Array<out SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}