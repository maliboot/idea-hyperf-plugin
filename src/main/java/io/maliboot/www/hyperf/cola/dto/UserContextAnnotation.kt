package io.maliboot.www.hyperf.cola.dto

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.www.hyperf.common.psi.data.CustomClass
import io.maliboot.www.hyperf.common.psi.data.CustomField
import io.maliboot.www.hyperf.common.psi.data.CustomMethod
import io.maliboot.www.hyperf.common.psi.data.CustomParameter
import io.maliboot.www.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode

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