package io.maliboot.www.hyperf.lombok.usages

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.findUsages.PhpFindUsagesHandlerFactory
import com.jetbrains.php.lang.findUsages.PhpFindUsagesOptions

// todo: not use => to ReferenceSearch
class SetterUsagesSearch(val project: Project) : PhpFindUsagesHandlerFactory(project) {
    override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler {
        return this.createFindUsagesHandler(
            element,
            if (forHighlightUsages) OperationMode.HIGHLIGHT_USAGES else OperationMode.DEFAULT
        )
    }

}