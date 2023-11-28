package io.maliboot.devkit.idea.hyperf.tool.ext

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.resolve.types.PhpType

fun PhpType.getPhpClass(project: Project, excludeDir: List<String> = BASE_EXCLUDE_DIRS): PhpClass? {
    val filterDelegate = this.toString().parseDelegatePhpType()

    // 默认取第一个类
    if (filterDelegate.contains("|")) {
        return filterDelegate.split("|")
            .filter { it.contains("\\") }
            .takeIf { it.isNotEmpty() }
            ?.first()
            ?.getPhpClass(project, excludeDir)
    }
    return filterDelegate.getPhpClass(project, excludeDir)
}

fun PhpType.parseGlobalType(): PhpType {
    return PhpType().add(this.toString().parseDelegatePhpType())
}

val PhpType.myFirstCompleteClassFQN: String?
    get() {
        return this.types.filter {
            it.contains("\\") && !it.contains(".")
        }.takeIf {
            it.isNotEmpty()
        }?.first()?.let {
            "\\" + it.substringAfter("\\")
        }
    }