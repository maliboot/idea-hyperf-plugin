package io.maliboot.devkit.idea.common.extend

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.PhpClass

val hyperfProxyFileSuffix: String
    get() = "proxy.php"

fun VirtualFile.getLineNumber(offset: Int): Int {
    return FileDocumentManager.getInstance().getDocument(this)?.getLineNumber(offset) ?: 0
}

fun VirtualFile.getHyperfProxyFile(project: Project): VirtualFile? {
    val psiFile = this.findPsiFile(project)
    if (psiFile !is PhpFile) {
        return null
    }
    val classFQN = PhpPsiUtil.findClass(psiFile, PhpClass.INSTANCEOF)?.fqn ?: return null
    return LocalFileSystem.getInstance().findFileByPath(
        project.getHyperfProxyDir() + "/" + classFQN.trimStart('\\').replace("\\", "_") + ".$hyperfProxyFileSuffix"
    )
}

fun VirtualFile.getHyperfOriginFile(project: Project): VirtualFile? {
    if (!this.name.contains(hyperfProxyFileSuffix)) {
        return this
    }
    val classFQN = "\\" + this.name.replace("_", "\\").substringBefore(".$hyperfProxyFileSuffix")
    PhpIndex.getInstance(project).getAnyByFQN(classFQN).filter { phpClass ->
        phpClass.containingFile.virtualFile.path.takeIf {
            !it.contains("/vendor/") && !it.contains("/proxy/")
        } != null
    }.takeIf { it.isNotEmpty() }?.let {
        return it.first().containingFile.virtualFile
    }
    return null
}