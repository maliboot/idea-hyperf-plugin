package io.maliboot.devkit.idea.cola.dto

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class IsPropertyInitAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {
    companion object {
        const val FEATURE = "isPropertyInitialized"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "isPropertyInitialized",
                PhpType.BOOLEAN,
                listOf(CustomParameter("property", PhpType.STRING)),
                PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC
            )
        )
    }
}