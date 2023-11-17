package io.maliboot.www.hyperf.lombok.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil
import com.jetbrains.php.lang.inspections.PhpUndefinedFieldInspection
import com.jetbrains.php.lang.inspections.PhpUndefinedMethodInspection
import com.jetbrains.php.lang.inspections.quickfix.PhpAddMethodDeclarationQuickFix
import com.jetbrains.php.lang.inspections.quickfix.PhpAddMethodTagQuickFix
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpCallableMethod
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import io.maliboot.www.hyperf.lombok.extend.getPhpClass
import io.maliboot.www.hyperf.lombok.psi.PhpClassEx


class MyUndefinedMethodInspection : PhpUndefinedMethodInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val superVisitor = super.buildVisitor(holder, isOnTheFly) as PhpElementVisitor
        return object : PhpElementVisitor() {
            override fun visitPhpMethodReference(reference: MethodReference) {
                if (reference.resolve() != null) {
                    return
                }
                val nameNode = reference.nameNode ?: return

                val phpClass = reference.classReference?.globalType?.getPhpClass(reference.project, listOf())
                val methodName = nameNode.psi.text
                if (phpClass != null) {
                    PhpClassEx(phpClass).findMethodByName(methodName)?.let {
                        return
                    }
                }
                superVisitor.visitPhpMethodReference(reference)

                // fix-链式操作时，自定义方法的后续方法无提示
                holder.results.none { it.psiElement.text.equals(methodName) }.takeIf { it }?.let {
                    val warningType =
                        if (hasMagic(reference)) ProblemHighlightType.WEAK_WARNING else ProblemHighlightType.WARNING
                    holder.registerProblem(
                        InspectionManager.getInstance(reference.project).createProblemDescriptor(
                            nameNode.psi,
                            "Method '$methodName' not found in ${phpClass?.name} ",
                            isOnTheFly,
                            getBaseFixes(reference),
                            warningType
                        )
                    )
                }
            }

            override fun visitPhpCallableMethod(reference: PhpCallableMethod) {
                superVisitor.visitPhpCallableMethod(reference)
            }

            private fun hasMagic(reference: MethodReference): Boolean {
                var hasMagic: Boolean
                val magicMethod = if (reference.isStatic) "__callStatic" else "__call"
                val index = PhpIndex.getInstance(reference.project)
                val type = reference.classReference?.globalType ?: return false
                hasMagic = PhpCodeInsightUtil.hasMagicMethod(type, index, magicMethod)
                if (!hasMagic) {
                    hasMagic = PhpUndefinedFieldInspection.isTraitWithMagicMethodsInAllUsages(type, index, magicMethod)
                }
                return hasMagic
            }

            private fun getBaseFixes(methodReference: PhpReference): Array<LocalQuickFix> {
                return if (PhpAddMethodDeclarationQuickFix.INSTANCE.isApplicable(methodReference)) arrayOf(
                    PhpAddMethodDeclarationQuickFix.INSTANCE,
                    PhpAddMethodTagQuickFix.INSTANCE
                ) else arrayOf(PhpAddMethodTagQuickFix.INSTANCE)
            }
        }
    }
}