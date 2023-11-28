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

class SetterAnnotation(classNode: PhpClassNode, fieldNode: PhpFieldNode) :
    AbstractFieldAnnotation(classNode, fieldNode) {

    companion object {
        const val FEATURE = "setter"
    }

    override fun getCustomField(): List<CustomField> {
        return emptyList()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        val setterName =
            "set" + phpFieldNode.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                setterName,
                PhpType().add(phpClassNode.type),
                listOf(CustomParameter(phpFieldNode.name, getPhpType(), phpFieldNode.default))
            )
        )
    }
}