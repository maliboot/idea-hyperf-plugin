package io.maliboot.www.hyperf.lombok.annotation.register

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.www.hyperf.common.psi.data.CustomClass
import io.maliboot.www.hyperf.common.psi.data.CustomField
import io.maliboot.www.hyperf.common.psi.data.CustomMethod
import io.maliboot.www.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode

class LoggerAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {

    companion object {
        const val FEATURE = "logger"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf(
            CustomField(
                FEATURE,
                CustomClass(phpClassNode.type),
                "logger",
                PhpType().add("\\Psr\\Log\\LoggerInterface")
            )
        )
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return emptyList()
    }
}