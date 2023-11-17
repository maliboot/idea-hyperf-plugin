package io.maliboot.www.hyperf.common.psi.data

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class CustomClass(val fqn: String) : CustomMember {
    override fun getNameCS(): String {
        return fqn.split('.').last()
    }

    override fun getReturnTypeCS(): PhpType {
        return PhpType().add(fqn)
    }

    override fun getFQN(): String {
        return fqn
    }

    override fun getFeatureName(): String {
        return fqn
    }

    override fun getModifierCS(): PhpModifier? {
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomClass

        return fqn == other.fqn
    }

    override fun hashCode(): Int {
        return fqn.hashCode()
    }
}