package io.maliboot.www.hyperf.lombok.codeInsight.parameter

import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.psi.PsiElementResolveResult
import com.jetbrains.php.lang.PhpParameterInfoHandler
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterListOwner
import io.maliboot.www.hyperf.lombok.extend.getPhpClass
import io.maliboot.www.hyperf.lombok.psi.PhpClassEx

class CustomMethodParameterInfo : PhpParameterInfoHandler() {
    override fun findElementForParameterInfo(context: CreateParameterInfoContext): ParameterListOwner? {
        val place = super.findElementForParameterInfo(context)
        if (place is MethodReference && place.resolve() == null) {
            val phpClass =
                place.globalType.getPhpClass(context.project) ?: return place
            PhpClassEx(phpClass).getFakeMethods()[phpClass.fqn + "." + place.name]?.let {
                context.itemsToShow = PsiElementResolveResult.createResults(it)
            }
        }

        return place
    }
}