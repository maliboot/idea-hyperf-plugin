package io.maliboot.devkit.idea.hyperf.lombok.annotation.register

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.hyperf.lombok.annotation.AbstractFieldAnnotation
import io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode.PhpFieldNode
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomClass
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMethod
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomParameter
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
        val propertyType = getPhpType()
        if (phpFieldNode.default == null && !propertyType.isNullable) {
            propertyType.add(PhpType.NULL)
        }

        val getterName =
            "get" + phpFieldNode.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                getterName,
                propertyType,
                if (phpFieldNode.default != null) listOf() else listOf(CustomParameter("default", propertyType, "null"))
            )
        )
    }
}