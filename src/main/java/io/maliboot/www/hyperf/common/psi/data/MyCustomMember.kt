package io.maliboot.www.hyperf.common.psi.data

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import io.maliboot.www.hyperf.lombok.extend.getPhpClass

class MyCustomMember(val fromFQN: String, val fromFile: String, val member: CustomMember) {
    fun getFromElement(project: Project): PhpNamedElement? {
        val from = fromFQN.split('.')
        if (from.size != 2) {
            return null
        }

        val phpClass = from[0].getPhpClass(project) ?: return null
        if (fromFQN.contains('$')) {
            for (field in phpClass.fields) {
                if (field.fqn == fromFQN) {
                    return field
                }
            }
        }

        for (method in phpClass.methods) {
            if (method.fqn == fromFQN) {
                return method
            }
        }

        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyCustomMember

        if (fromFQN != other.fromFQN) return false
        if (fromFile != other.fromFile) return false
        if (member != other.member) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fromFQN.hashCode()
        result = 31 * result + fromFile.hashCode()
        result = 31 * result + member.hashCode()
        return result
    }

}