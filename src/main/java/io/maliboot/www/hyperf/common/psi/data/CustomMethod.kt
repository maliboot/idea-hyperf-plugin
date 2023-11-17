package io.maliboot.www.hyperf.common.psi.data

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class CustomMethod(
    val feature: String,
    val clazz: CustomClass,
    val name: String,
    val returnType: PhpType,
    val parameters: List<CustomParameter>,
    val modifier: PhpModifier = PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC
) : CustomMember {
    private fun parametersAsStringList(project: Project): List<String> {
        return parameters.map {
            var parameterAsString = "${it.returnType.global(project).toStringResolved()} $${it.name}"

            if (it.defaultValue !== null) {
                parameterAsString += " = ${it.defaultValue}"
            }

            parameterAsString
        }
    }

    fun parametersAsString(project: Project): String {
        return parametersAsStringList(project).joinToString()
    }

    override fun toString(): String {
        return "Method(name='$name', returnType=$returnType, parameters=$parameters)"
    }

    override fun getNameCS(): String {
        return name
    }

    override fun getReturnTypeCS(): PhpType {
        return returnType
    }

    override fun getModifierCS(): PhpModifier {
        return modifier
    }

    override fun getFQN(): String {
        return clazz.fqn + "." + name
    }

    override fun getFeatureName(): String {
        return feature
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomMethod

        if (feature != other.feature) return false
        if (clazz != other.clazz) return false
        if (name != other.name) return false
        if (returnType != other.returnType) return false
        if (parameters != other.parameters) return false
        if (modifier != other.modifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feature.hashCode()
        result = 31 * result + clazz.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}