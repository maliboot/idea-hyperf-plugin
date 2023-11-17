package io.maliboot.www.hyperf.lombok.annotation.register

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.www.hyperf.common.psi.data.CustomClass
import io.maliboot.www.hyperf.common.psi.data.CustomField
import io.maliboot.www.hyperf.common.psi.data.CustomMethod
import io.maliboot.www.hyperf.common.psi.data.CustomParameter
import io.maliboot.www.hyperf.lombok.LombokCollector
import io.maliboot.www.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode

open class DelegateAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {

    companion object {
        const val FEATURE = "myDelegate"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf(
            CustomField(
                FEATURE,
                CustomClass(phpClassNode.type),
                "myDelegate",
                PhpType().add(getDelegateClassName()),
                null,
                PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC
            )
        )
    }

    private fun getDelegateClassName(): String {
        val delegateAttrs = phpClassNode.attributes
        if (delegateAttrs.isEmpty()) {
            return "mixed"
        }
        for (attr in delegateAttrs) {
            if (attr.type == "\\MaliBoot\\Cola\\Annotation\\Database") {
                return "\\MaliBoot\\Cola\\Infra\\AbstractModelDelegate"
            }
            if (attr.type == LombokCollector.DELEGATE) {
                attr.args.takeIf { it.isNotEmpty() }?.first()?.trim()?.removeSuffix("::class")?.substringAfter(":")
                    ?.let { delegateClassName ->
                        return phpClassNode.getAnyClassFQNByName(
                            delegateClassName.removeSurrounding("'").removeSurrounding("\"")
                        )
                    }
            }
        }

        return "mixed"
    }

    override fun getCustomMethod(): List<CustomMethod> {
        val delegatePhpType = PhpType().add(getDelegateClassName())
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "getMyDelegate",
                delegatePhpType,
                listOf()
            ),
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "setMyDelegate",
                PhpType().add(phpClassNode.type),
                listOf(CustomParameter("delegate", delegatePhpType))
            )
        )
    }
}