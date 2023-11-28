package io.maliboot.devkit.idea.hyperf.lombok.annotation.register

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomClass
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMethod

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