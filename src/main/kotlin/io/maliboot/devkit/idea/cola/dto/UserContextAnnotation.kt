package io.maliboot.devkit.idea.cola.dto

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class UserContextAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {
    companion object {
        const val FEATURE = "userContext"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf(
            CustomField(
                FEATURE,
                CustomClass(phpClassNode.type),
                "_context",
                PhpType().add("\\MaliBoot\\Dto\\UserContext").add(PhpType.NULL),
                null,
                PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC
            )
        )
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "user",
                PhpType().add("\\MaliBoot\\Dto\\UserContext").add(PhpType.NULL),
                listOf()
            ),
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "getUser",
                PhpType().add("\\MaliBoot\\Dto\\UserContext").add(PhpType.NULL),
                listOf()
            ),
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "setUser",
                PhpType().add(phpClassNode.type),
                listOf(CustomParameter("user", PhpType().add("\\MaliBoot\\Dto\\UserContext").add(PhpType.NULL)))
            )
        )
    }
}