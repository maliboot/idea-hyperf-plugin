package io.maliboot.devkit.idea.lombok.annotation.register

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class OfAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {

    companion object {
        const val FEATURE = "of"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "of",
                PhpType().add(phpClassNode.type),
                listOf(CustomParameter("fieldData", PhpType.ARRAY)),
                PhpModifier.PUBLIC_IMPLEMENTED_STATIC
            ),
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "ofData",
                PhpType().add(phpClassNode.type),
                listOf(CustomParameter("fieldData", PhpType.ARRAY)),
                PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC
            )
        )
    }
}