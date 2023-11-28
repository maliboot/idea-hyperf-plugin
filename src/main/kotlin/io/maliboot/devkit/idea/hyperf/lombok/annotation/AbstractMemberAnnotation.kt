package io.maliboot.devkit.idea.hyperf.lombok.annotation

import io.maliboot.devkit.idea.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMember
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMethod

abstract class AbstractMemberAnnotation(val phpClassNode: PhpClassNode) {
    fun getCustomMembers(): Map<String, CustomMember> {
        if (!enable()) {
            return emptyMap()
        }

        val result = mutableMapOf<String, CustomMember>()
        getCustomField()
            .takeIf { it.isNotEmpty() }
            ?.filter { customMember ->
                phpClassNode.fields.none { it.name == customMember.name }
            }?.let { customMembers ->
                result.putAll(customMembers.associateBy { it.getFQN() })
            }

        getCustomMethod()
            .takeIf { it.isNotEmpty() }
            ?.filter { customMember ->
                phpClassNode.methods.none { it.name == customMember.name }
            }?.let { customMembers ->
                result.putAll(customMembers.associateBy { it.getFQN() })
            }

        return result
    }

    open fun enable(): Boolean {
        return true
    }

    protected abstract fun getCustomField(): List<CustomField>

    protected abstract fun getCustomMethod(): List<CustomMethod>
}