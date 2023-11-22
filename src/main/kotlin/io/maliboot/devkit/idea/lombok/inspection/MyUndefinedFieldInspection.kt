package io.maliboot.devkit.idea.lombok.inspection

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpUndefinedClassVisitorBase
import com.jetbrains.php.lang.inspections.PhpUndefinedFieldInspection
import com.jetbrains.php.lang.psi.elements.FieldReference
import io.maliboot.devkit.idea.lombok.extend.getPhpClass
import io.maliboot.devkit.idea.lombok.psi.FakePsiPhpFieldReference
import io.maliboot.devkit.idea.lombok.psi.PhpClassEx

class MyUndefinedFieldInspection : PhpUndefinedFieldInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val parentVisitor = super.buildVisitor(holder, isOnTheFly) as PhpUndefinedClassVisitorBase
        return object : PhpUndefinedClassVisitorBase(holder, isOnTheFly, super.isWarnOnMixed()) {
            override fun visitPhpFieldReference(reference: FieldReference) {

                if (reference.resolve() != null) {
                    return
                }
                val phpClass = reference.classReference?.globalType?.getPhpClass(reference.project)
                val fieldName = reference.nameNode?.psi?.text ?: ""
                if (phpClass != null) {
                    PhpClassEx(phpClass).findFakeFieldByName(fieldName)?.let {
                        parentVisitor.visitPhpFieldReference(FakePsiPhpFieldReference(reference, it))
                        return
                    }
                }

                parentVisitor.visitPhpFieldReference(reference)
//
//                // fix-链式操作时，自定义方法的后续方法无提示
//                holder.results.none { it.psiElement.text.equals(fieldName) }.takeIf { it }?.let {
//                    val warningType = if (hasMagic(reference)) ProblemHighlightType.WEAK_WARNING else ProblemHighlightType.WARNING
//                    holder.registerProblem(
//                        InspectionManager.getInstance(reference.project).createProblemDescriptor(
//                        reference.nameNode!!.psi,
//                        "Property '$fieldName' not found in ${phpClass?.name} ",
//                        isOnTheFly,
//                        getBaseFixes(reference),
//                        warningType
//                    ))
//                }
            }
        }
    }
}