package io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode

class PhpFieldNode(
    val name: String,
    val type: String,
    val default: String? = null,
    val attributes: List<PhpAttribute> = emptyList()
)