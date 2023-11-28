package io.maliboot.devkit.idea.hyperf.lombok.inspection

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.type.PhpStrictTypeCheckingInspection
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import io.maliboot.devkit.idea.hyperf.tool.ext.getPhpClassEx
import io.maliboot.devkit.idea.hyperf.tool.ext.myFirstCompleteClassFQN
import io.maliboot.devkit.idea.hyperf.lombok.psi.FakePsiPhpMethodReference

class MyPhpStrictTypeCheckingInspection : PhpStrictTypeCheckingInspection() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        val parentVisitor = super.buildVisitor(holder, isOnTheFly, session)
        if (parentVisitor !is PhpElementVisitor) {
            return parentVisitor
        }
        return object : PhpElementVisitor() {
            override fun visitPhpReturn(returnStatement: PhpReturn?) {
                parentVisitor.visitPhpReturn(returnStatement)
            }

            override fun visitPhpFunction(function: Function?) {
                parentVisitor.visitPhpFunction(function)
            }

            override fun visitPhpFunctionCall(reference: FunctionReference?) {
                parentVisitor.visitPhpFunctionCall(reference)
            }

            override fun visitPhpMethodReference(reference: MethodReference) {
                reference.classReference
                    ?.globalType
                    ?.myFirstCompleteClassFQN
                    ?.getPhpClassEx(reference.project)
                    ?.findFakeMethodByName(reference.name ?: "")
                    ?.let { fakeMethod ->
                        FakePsiPhpMethodReference(reference, fakeMethod)
                    }?.let {
                        parentVisitor.visitPhpMethodReference(it)
                        return
                    }
                parentVisitor.visitPhpMethodReference(reference)
            }

            override fun visitPhpNewExpression(expression: NewExpression) {
                parentVisitor.visitPhpNewExpression(expression)
            }

            override fun visitPhpAttribute(attribute: PhpAttribute) {
                parentVisitor.visitPhpAttribute(attribute)
            }

            override fun visitPhpAssignmentExpression(assignmentExpression: AssignmentExpression) {
                parentVisitor.visitPhpAssignmentExpression(assignmentExpression)
            }
        }
    }
}