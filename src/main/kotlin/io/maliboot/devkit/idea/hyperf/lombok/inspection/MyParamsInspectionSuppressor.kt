package io.maliboot.devkit.idea.hyperf.lombok.inspection

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.inspections.type.PhpParamsInspection
import com.jetbrains.php.lang.inspections.type.PhpStrictTypeCheckingInspection

class MyParamsInspectionSuppressor : InspectionSuppressor {

    companion object {
        private val SUPPRESSED_PHP_INSPECTIONS = listOf(
            PhpParamsInspection().id,
            PhpStrictTypeCheckingInspection().id
        )
    }

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (!SUPPRESSED_PHP_INSPECTIONS.contains(toolId)) {
            return false
        }

        return SUPPRESSED_PHP_INSPECTIONS.contains(toolId)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}