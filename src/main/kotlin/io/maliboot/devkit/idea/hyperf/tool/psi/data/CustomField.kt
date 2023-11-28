package io.maliboot.devkit.idea.hyperf.tool.psi.data

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class CustomField(
    val feature: String,
    val clazz: CustomClass,
    val name: String,
    val returnType: PhpType,
    val defaultValue: String? = null,
    val modifier: PhpModifier = PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC
) : CustomMember {

    override fun getNameCS(): String {
        return name
    }

    override fun getReturnTypeCS(): PhpType {
        return returnType
    }

    override fun getFQN(): String {
        return clazz.fqn + ".\$" + name
    }

    override fun getFeatureName(): String {
        return feature
    }

    override fun getModifierCS(): PhpModifier {
        return modifier
    }

    override fun toString(): String {
        return "CustomField(name='$name', returnType=$returnType, defaultValue=$defaultValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomField

        if (feature != other.feature) return false
        if (clazz != other.clazz) return false
        if (name != other.name) return false
        if (returnType != other.returnType) return false
        if (defaultValue != other.defaultValue) return false
        if (modifier != other.modifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feature.hashCode()
        result = 31 * result + clazz.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        result = 31 * result + modifier.hashCode()
        return result
    }
}