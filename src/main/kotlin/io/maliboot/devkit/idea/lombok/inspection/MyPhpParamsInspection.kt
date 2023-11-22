package io.maliboot.devkit.idea.lombok.inspection

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.ResolveResult
import com.jetbrains.php.config.library.PhpRuntimeLibraryRootsProvider
import com.jetbrains.php.lang.inspections.type.PhpParamsInspection
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import io.maliboot.devkit.idea.lombok.extend.getPhpClassEx
import io.maliboot.devkit.idea.lombok.extend.myFirstCompleteClassFQN
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class MyPhpParamsInspection : PhpParamsInspection() {

    object Util {
        fun myCheckCall(
            call: PhpPsiElement,
            results: Array<ResolveResult>,
            callParams: Array<PsiElement?>,
            extensionRoots: Set<VirtualFile?>,
            holder: ProblemsHolder
        ) {
            PhpParamsInspection::class.declaredFunctions.find {
                it.name == "checkCall"
            }?.let {
                it.isAccessible = true
                try {
                    it.call(call, results, callParams, extensionRoots, holder)
                } catch (_: Exception) {
                    // do nothing
                }
            }
        }
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val parentVisitor = super.buildVisitor(holder, isOnTheFly) as PhpElementVisitor
        return object : PhpElementVisitor() {
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
                        Util.myCheckCall(
                            reference,
                            PsiElementResolveResult.createResults(fakeMethod),
                            reference.parameters,
                            PhpRuntimeLibraryRootsProvider.getLibraryRoots(holder.project),
                            holder
                        )
                        return
                    }
                parentVisitor.visitPhpMethodReference(reference)
            }

            override fun visitPhpNewExpression(expression: NewExpression?) {
                parentVisitor.visitPhpNewExpression(expression)
            }

            override fun visitPhpAttribute(attribute: PhpAttribute?) {
                parentVisitor.visitPhpAttribute(attribute)
            }
        }
    }
}