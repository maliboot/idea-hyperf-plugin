package io.maliboot.devkit.idea.hyperf.tool.ext

import com.intellij.openapi.project.Project

fun Project.getHyperfProxyDir(relativePath: String = "/runtime/container/proxy"): String {
    return this.basePath + relativePath
}