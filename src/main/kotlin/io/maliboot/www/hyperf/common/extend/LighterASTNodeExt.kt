package io.maliboot.www.hyperf.common.extend

import com.intellij.lang.LighterASTNode
import com.intellij.openapi.vfs.VirtualFile

fun LighterASTNode.getStartLineNumber(file: VirtualFile): Int {
    return file.getLineNumber(this.startOffset)
}