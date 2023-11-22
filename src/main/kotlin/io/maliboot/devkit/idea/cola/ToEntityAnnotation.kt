package io.maliboot.devkit.idea.cola

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class ToEntityAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {
    companion object {
        const val FEATURE = "ToEntity"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "toEntity",
                PhpType.OBJECT,
                listOf(CustomParameter("entityFQN", PhpType().add(PhpType.NULL).add(PhpType.STRING), "null")),
                PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC
            )
        )
    }
}