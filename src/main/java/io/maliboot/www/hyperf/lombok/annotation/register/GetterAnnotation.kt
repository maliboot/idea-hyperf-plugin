package io.maliboot.www.hyperf.lombok.annotation.register

import io.maliboot.www.hyperf.common.psi.data.CustomClass
import io.maliboot.www.hyperf.common.psi.data.CustomField
import io.maliboot.www.hyperf.common.psi.data.CustomMethod
import io.maliboot.www.hyperf.lombok.annotation.AbstractFieldAnnotation
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpFieldNode
import java.util.*

internal class GetterAnnotation(classNode: PhpClassNode, fieldNode: PhpFieldNode) :
    AbstractFieldAnnotation(classNode, fieldNode) {
    companion object {
        const val FEATURE = "getter"
    }

    override fun getCustomField(): List<CustomField> {
        return emptyList()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        val getterName =
            "get" + phpFieldNode.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                getterName,
                getPhpType(),
                emptyList()
            )
        )
    }
}