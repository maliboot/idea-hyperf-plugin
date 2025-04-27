package io.maliboot.devkit.idea.lombok.annotation.lightNode

class PhpClassNode(
    val name: String,
    val type: String,
    val uses: Map<String, String> = emptyMap(),
    val fields: List<PhpFieldNode> = emptyList(),
    val methods: List<PhpMethodNode> = emptyList(),
    val attributes: List<PhpAttribute> = emptyList()
) {
    fun getAnyClassFQNByName(className: String): String {
        if (className.startsWith("\\")) {
            return className
        }
        uses[className]?.let { return it }

        return getNamespace() + "\\" + className
    }

    fun getNamespace(): String {
        return type.substringBefore("\\$name")
    }
}