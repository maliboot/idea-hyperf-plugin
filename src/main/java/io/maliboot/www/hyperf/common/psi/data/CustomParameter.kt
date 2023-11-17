package io.maliboot.www.hyperf.common.psi.data

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType

open class CustomParameter(open val name: String, open val returnType: PhpType, open val defaultValue: String? = null) :
    CustomMember {
    override fun toString(): String {
        val defaultValue = defaultValue ?: ""

        return "Parameter(name='$name', returnType='$returnType', defaultValue='$defaultValue')"
    }

    fun asText(): String {
        val defaultValue = if (defaultValue == null) "" else "=$defaultValue"

        return "$returnType \$$name $defaultValue"
    }

    override fun getNameCS(): String {
        return name
    }

    override fun getReturnTypeCS(): PhpType {
        return returnType
    }

    override fun getFQN(): String {
        return ""
    }

    override fun getFeatureName(): String {
        return ""
    }

    override fun getModifierCS(): PhpModifier? {
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomParameter

        if (name != other.name) return false
        if (returnType != other.returnType) return false
        if (defaultValue != other.defaultValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        return result
    }
}