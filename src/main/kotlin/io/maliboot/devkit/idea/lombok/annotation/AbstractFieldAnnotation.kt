package io.maliboot.devkit.idea.lombok.annotation

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpFieldNode

abstract class AbstractFieldAnnotation(phpClassNode: PhpClassNode, val phpFieldNode: PhpFieldNode) :
    AbstractMemberAnnotation(phpClassNode) {
    fun getPhpType(): PhpType {
        var myFieldType = PhpType()
        phpFieldNode.type.split("|").forEach {
            myFieldType = myFieldType.add(it)
        }
        return myFieldType
    }
}