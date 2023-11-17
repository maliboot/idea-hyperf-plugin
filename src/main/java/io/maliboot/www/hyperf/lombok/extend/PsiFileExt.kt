package io.maliboot.www.hyperf.lombok.extend

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import io.maliboot.www.hyperf.common.extend.BASE_EXCLUDE_DIRS

val PsiFile.realPath: String
    get() {
        var virtualFile = this.viewProvider.virtualFile
        if (virtualFile is LightVirtualFile && virtualFile.originalFile != null) {
            virtualFile = virtualFile.originalFile
        }
        return virtualFile.path
    }

fun PsiFile.getPhpClass(): PhpClass? {
    var phpClass: PhpClass? = null
    if (this !is PhpFile) {
        return null
    }

    accept(object : PsiRecursiveElementWalkingVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element !is PhpClass) {
                super.visitElement(element)
                return
            }
            phpClass = element
            stopWalking()
        }
    })
    return phpClass
}

fun PsiFile.isExcludeDir(excludeDir: List<String> = BASE_EXCLUDE_DIRS): Boolean {
    return excludeDir.none { this.realPath.contains("${this.project.basePath}/$it") }
}