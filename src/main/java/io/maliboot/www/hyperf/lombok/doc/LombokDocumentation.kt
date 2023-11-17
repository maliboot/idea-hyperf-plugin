package io.maliboot.www.hyperf.lombok.doc

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.documentation.PhpDocumentationProvider
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import io.maliboot.www.hyperf.lombok.extend.getPhpClass
import io.maliboot.www.hyperf.lombok.psi.PhpClassEx
import io.maliboot.www.hyperf.lombok.typeProvider.CustomMemberTypeProvider
import org.jetbrains.annotations.Nls

class LombokDocumentation : PhpDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (element !is MemberReference) {
            return myGenerateDoc(element, originalElement)
        }

        // 取上一级的返回
        val elementName = element.name ?: return myGenerateDoc(element, originalElement)
        val phpClass: PhpClass =
            element.classReference?.globalType?.getPhpClass(element.project) ?: return myGenerateDoc(
                element,
                originalElement
            )
        val phpClassEx = PhpClassEx(phpClass)
        if (element is FieldReference) {
            return myGenerateDoc((phpClassEx.findFieldByName(elementName) ?: element), originalElement)
        }
        if (element is MethodReference) {
            return myGenerateDoc((phpClassEx.findMethodByName(elementName) ?: element), originalElement)
        }

        return myGenerateDoc(phpClass, originalElement)
    }

    fun myGenerateDoc(element: PsiElement, originalElement: PsiElement?): @Nls String? {
        try {
            val originDoc = super.generateDoc(element, originalElement) ?: return null
            val matchStr = Regex(""">#.*?<""").find(originDoc)?.value ?: return originDoc
            val newStr = matchStr.let {
                val sign = it.substring(1, it.length - 1)
                if (!sign.contains(CustomMemberTypeProvider.ID)) {
                    it
                } else {
                    sign.split('|').last().let { s -> ">$s<" }
                }
            }
            return originDoc.replace(matchStr, newStr)
        } catch (_: Exception) {
            return null
        }
    }
}