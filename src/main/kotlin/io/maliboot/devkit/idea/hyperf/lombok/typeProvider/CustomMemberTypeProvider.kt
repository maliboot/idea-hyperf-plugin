package io.maliboot.devkit.idea.hyperf.lombok.typeProvider

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.Strings
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpCaches
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import io.maliboot.devkit.idea.hyperf.tool.ext.getPhpClass
import io.maliboot.devkit.idea.hyperf.tool.ext.getPhpClassEx
import io.maliboot.devkit.idea.hyperf.tool.ext.isExcludeDir
import io.maliboot.devkit.idea.hyperf.tool.ext.myFirstCompleteClassFQN
import io.maliboot.devkit.idea.hyperf.lombok.psi.PhpClassEx

class CustomMemberTypeProvider : PhpTypeProvider4 {
    companion object {
        const val ID = '马'
        private val logger: Logger = Logger.getInstance(CustomMemberTypeProvider::class.java)
    }

    override fun getKey(): Char {
        return ID
    }

    override fun getType(p0: PsiElement?): PhpType? {
        if (p0 == null || DumbService.getInstance(p0.project).isDumb || !p0.containingFile.isExcludeDir()) {
            return null
        }

        var isFieldReference = false
        val reference: MemberReference = when (p0) {
            is FieldReference -> {
                isFieldReference = true
                p0
            }

            is MethodReference -> {
                p0
            }

            else -> null
        } ?: return null
        if (reference.name === null) {
            return null
        }

        val classReference = reference.classReference ?: return null
        val classRefFQN = classReference.type.toString().removeSuffix("|?").split("|").last()
        when (classReference) {
            is Variable, is ClassReference, is MemberReference, is FunctionReference -> {
                val flag = if (isFieldReference) "\$" else ""
                val prefix = if (classRefFQN.contains("#$key")) "" else "#$key"
                return PhpType().add("$prefix${classRefFQN}@$flag${reference.name}")
            }
        }

        return null
    }

    override fun complete(p0: String?, p1: Project?): PhpType? {
        if (p0 == null || p1 == null) {
            return null
        }
        return ReadAction.compute<PhpType?, RuntimeException> {
            val removePrefixSign = p0.removePrefix("#$key")
            val atIndex = removePrefixSign.indexOf('@')
            if (atIndex == -1) {
                return@compute null
            }
            val clazzSign = removePrefixSign.substring(0, atIndex)
            val clazzMemberSign = removePrefixSign.substring(atIndex).replace('@', '.')

            var clazzFQN = clazzSign
            if (clazzSign.contains("#")) {
                PhpIndex.getInstance(p1).getBySignature(clazzSign).takeIf { it.isNotEmpty() }?.first()?.let {
                    clazzFQN = it.fqn
                }
            }
            val newSign = clazzFQN + clazzMemberSign
            val result = PhpType()
            this.getBySignatureInternal(newSign, p1).takeIf { it.isNotEmpty() }?.forEach {
                result.add(it.type)
            }
            PhpCaches.getInstance(p1).TYPE_COMPLETION_CACHE["${key}$newSign"] = result
            return@compute if (result.isEmpty) null else result
        }
    }

    private fun lookupRecursionByFQN(fqn: String, project: Project): PhpNamedElement? {
        val phpMemberNames = fqn.substringAfter(".").split(".")

        var phpClassFQN = fqn.substringBefore(".")
        val phpClass = phpClassFQN.getPhpClass(project) ?: return null
        if (phpMemberNames.isEmpty()) {
            return phpClass
        }

        val phpClassExs = mutableMapOf(phpClass.fqn to PhpClassEx(phpClass))
        for ((memberNameIndex, memberName) in phpMemberNames.withIndex()) {
            val memberElement = if (memberName.contains("\$")) {
                phpClassExs[phpClassFQN]?.findFieldByName(memberName.substring(1))
            } else {
                phpClassExs[phpClassFQN]?.findMethodByName(memberName)
            } ?: return null

            if (memberNameIndex == phpMemberNames.size - 1) {
                return memberElement
            }

            memberElement.type.myFirstCompleteClassFQN?.getPhpClassEx(project)?.let {
                phpClassFQN = it.myPhpClass.fqn
                phpClassExs.put(phpClassFQN, it)
            }
        }

        return null
    }

    private fun getBySignatureInternal(fqn: String, project: Project): MutableCollection<out PhpNamedElement> {
        // try cache
        val phpSignCaches = PhpCaches.getInstance(project).SIGNATURES_CACHE
        phpSignCaches["#$key$fqn"]?.takeIf { it.isNotEmpty() }?.let { return it }

        val result: MutableList<PhpNamedElement> = mutableListOf()

        // return class
        if (Strings.countChars(fqn, '.') <= 1) {
            this.lookupRecursionByFQN(fqn, project)?.let { mutableListOf(it) }?.let {
                result.add(it.first())
            }
        } else {
            // try classReference cache
            val lastPointIndex = fqn.lastIndexOf('.')
            val lastFQN = fqn.substring(0, lastPointIndex)
            var lastSignElements = phpSignCaches["#$key$lastFQN"]
            if (lastSignElements.isNullOrEmpty()) {
                lastSignElements = this.getBySignatureInternal(lastFQN, project)
            }
            if (lastSignElements.isEmpty()) {
                return mutableListOf()
            }

            lastSignElements.first().type.myFirstCompleteClassFQN?.let {
                it + fqn.substring(lastPointIndex)
            }?.let {
                this.lookupRecursionByFQN(it, project)
            }?.let {
                result.add(it)
            }
        }

        phpSignCaches["#$key$fqn"] = result
        return result
    }

    override fun getBySignature(
        p0: String?,
        p1: MutableSet<String>?,
        p2: Int,
        p3: Project?
    ): MutableCollection<out PhpNamedElement> {
        return mutableListOf()
    }
}