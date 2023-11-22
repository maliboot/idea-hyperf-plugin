package io.maliboot.devkit.idea.lombok.psi

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.MyCustomMember
import io.maliboot.devkit.idea.lombok.extend.getPhpClass
import io.maliboot.devkit.idea.lombok.index.CustomMemberPsiGist

class PhpClassEx(val myPhpClass: PhpClass) {

    companion object {
        val delegateTo: Key<MutableSet<String>> = Key.create("lombok.delegateTo")
    }

    val myDelegate: PhpClass?
        get() = getDelegate()

    fun findMethodByName(name: CharSequence): Method? {
        myPhpClass.findMethodByName(name)?.let { return it }
        return findFakeMethodByName(name)
    }

    fun findFieldByName(name: CharSequence): Field? {
        myPhpClass.findFieldByName(name, true)?.let { return it }
        return findFakeFieldByName(name)
    }

    fun findFakeMethodByName(name: CharSequence): FakePsiPhpMethod? {
        return getFakeMethods()["${myPhpClass.fqn}.$name"]?.let { it as FakePsiPhpMethod }
    }

    fun findFakeFieldByName(name: CharSequence): FakePsiPhpField? {
        return getFakeFields()["${myPhpClass.fqn}.\$$name"]?.let { it as FakePsiPhpField }
    }

    fun getFakeMethods(): Map<String, Method> {
        val result: MutableMap<String, Method> = mutableMapOf()
        getCustomMembers().takeIf { it.isNotEmpty() }?.forEach {
            when (val myCustom = it.value.member) {
                is CustomMethod -> {
                    result[it.key] = FakePsiPhpMethod(myCustom, myPhpClass, it.value.fromFQN)
                }
            }
        }
        return result
    }

    fun getFakeFields(): Map<String, Field> {
        val result: MutableMap<String, Field> = mutableMapOf()
        getCustomMembers().takeIf { it.isNotEmpty() }?.forEach {
            when (val myCustom = it.value.member) {
                is CustomField -> {
                    result[it.key] = FakePsiPhpField(myCustom, myPhpClass, it.value.fromFQN)
                }
            }
        }
        return result
    }

    private fun getDelegate(): PhpClass? {
        return getCustomMembers()["${myPhpClass.fqn}.\$myDelegate"]
            ?.member
            ?.getReturnTypeCS()
            ?.getPhpClass(myPhpClass.project)
            ?.let { delegateClass ->
                (delegateClass.getUserData(delegateTo) ?: mutableSetOf()).let {
                    it.add(myPhpClass.fqn)
                    it
                }.let {
                    delegateClass.putUserData(delegateTo, it)
                }
                delegateClass
            }
    }

    fun getCustomMembers(): Map<String, MyCustomMember> {
        return CustomMemberPsiGist.getCustomMembers(myPhpClass.containingFile)
    }
}