package io.maliboot.devkit.idea.hyperf.tool.ext

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.rd.util.first
import io.maliboot.devkit.idea.hyperf.lombok.psi.PhpClassEx
import io.maliboot.devkit.idea.hyperf.lombok.typeProvider.CustomMemberTypeProvider

fun String.getPhpClass(project: Project, excludeDir: List<String> = BASE_EXCLUDE_DIRS): PhpClass? {
    if (!this.startsWith("\\", 0)) {
        return null
    }
    var classes = PhpIndex.getInstance(project).getAnyByFQN(this)
    if (excludeDir.isNotEmpty()) {
        val bathPath = project.basePath ?: return null
        classes = classes.filter { phpClass ->
            excludeDir.none { phpClass.containingFile.realPath.contains("$bathPath/$it") }
        }
    }

    val result: MutableMap<String, PhpClass> = mutableMapOf()
    // 当重复文件出现时，排除库、代理......相关文件
    for (clazz in classes) {
        if (!result.containsKey(clazz.fqn)) {
            result[clazz.fqn] = clazz
            continue
        }
        clazz.containingFile.realPath.takeIf {
            !it.contains("/vendor/") && !it.contains("/proxy/")
        }?.let { result[clazz.fqn] = clazz }
    }
    return result.takeIf { it.isNotEmpty() }?.first()?.value
}

fun String.getPhpClassEx(project: Project, excludeDir: List<String> = emptyList()): PhpClassEx? {
    return this.getPhpClass(project, excludeDir)?.let { PhpClassEx(it) }
}

fun String.getPhpClasses(project: Project): List<PhpClass> {
    if (!this.startsWith("\\", 0)) {
        return emptyList()
    }
    return PhpIndex.getInstance(project).getAnyByFQN(this).toList()
}

fun String.parseDelegatePhpType(): String {
    return this.let {
        // 过滤委托类
        if (it.contains("@${CustomMemberTypeProvider.ID}")) {
            it.split('@')[2]
        } else {
            it
        }
    }
}

fun String.getPhpElementByFQN(project: Project, phpClass: PhpClass? = null): PhpNamedElement? {
    val from = this.split('.')
    if (from.size != 2) {
        return null
    }

    val myPhpClass = phpClass ?: from[0].getPhpClass(project) ?: return null
    if (this.contains('$')) {
        for (field in myPhpClass.fields) {
            if (field.fqn == this) {
                return field
            }
        }
    }

    for (method in myPhpClass.methods) {
        if (method.fqn == this) {
            return method
        }
    }

    return null
}