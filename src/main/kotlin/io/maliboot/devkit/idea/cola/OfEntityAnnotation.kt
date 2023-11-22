package io.maliboot.devkit.idea.cola

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpType.NULL
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class OfEntityAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {
    companion object {
        const val FEATURE = "ofEntity"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "ofEntity",
                PhpType().add(phpClassNode.type).add(NULL),
                listOf(CustomParameter("entity", PhpType.OBJECT)),
                PhpModifier.PUBLIC_IMPLEMENTED_STATIC
            )
        )
    }
}