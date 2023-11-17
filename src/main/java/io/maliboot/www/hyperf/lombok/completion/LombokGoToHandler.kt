package io.maliboot.www.hyperf.lombok.completion

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import io.maliboot.www.hyperf.lombok.extend.getPhpClass
import io.maliboot.www.hyperf.lombok.psi.PhpClassEx

class LombokGoToHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<out PsiElement?>? {
        if (sourceElement == null || sourceElement.elementType != PhpTokenTypes.IDENTIFIER) {
            return PsiElement.EMPTY_ARRAY
        }

        val parent: PsiElement = sourceElement.parent
        if (parent !is MethodReference && parent !is FieldReference) {
            return PsiElement.EMPTY_ARRAY
        }
        parent as MemberReference

        val phpClass =
            parent.classReference?.globalType?.getPhpClass(sourceElement.project)
                ?: return PsiElement.EMPTY_ARRAY
        val phpClassEx = PhpClassEx(phpClass)

        if (parent is FieldReference) {
            phpClassEx.findFakeFieldByName(sourceElement.text)?.getGeneratedElement()
                ?.let { return arrayOf(it.identifyingElement) }
        }
        if (parent is MethodReference) {
            phpClassEx.findFakeMethodByName(sourceElement.text)?.getGeneratedElement()
                ?.let { return arrayOf(it.identifyingElement) }
        }

        return PsiElement.EMPTY_ARRAY
    }
}