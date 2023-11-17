package io.maliboot.www.hyperf.common.extend

import com.intellij.openapi.project.Project

fun Project.getHyperfProxyDir(relativePath: String = "/runtime/container/proxy"): String {
    return this.basePath + relativePath
}