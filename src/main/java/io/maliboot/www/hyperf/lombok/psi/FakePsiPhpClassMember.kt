package io.maliboot.www.hyperf.lombok.psi

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

interface FakePsiPhpClassMember {
    fun getGeneratedElement(): PhpNamedElement? {
        return null
    }

    fun getDocumentation(): String {
        return ""
    }

    fun getFeatureName(): String {
        return ""
    }
}