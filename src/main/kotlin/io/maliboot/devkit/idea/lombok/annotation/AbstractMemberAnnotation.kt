package io.maliboot.devkit.idea.lombok.annotation

import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMember
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

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