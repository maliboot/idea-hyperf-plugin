package io.maliboot.devkit.idea.hyperf.lombok.annotation.register

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomClass
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMethod

class ToArrayAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {

    companion object {
        const val FEATURE = "toArray"
    }

    override fun getCustomField(): List<CustomField> {
        return listOf()
    }

    override fun getCustomMethod(): List<CustomMethod> {
        return listOf(
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "toArray",
                PhpType.ARRAY,
                listOf()
            ),
            CustomMethod(
                FEATURE,
                CustomClass(phpClassNode.type),
                "all",
                PhpType.ARRAY,
                listOf()
            )
        )
    }
}