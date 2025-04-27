package io.maliboot.devkit.idea.common.psi.data

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType

interface CustomMember {
    fun getNameCS(): String

    fun getReturnTypeCS(): PhpType

    fun getFQN(): String

    fun getFeatureName(): String

    fun getModifierCS(): PhpModifier?
}