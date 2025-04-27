package io.maliboot.devkit.idea.lombok.structView

import com.intellij.ide.structureView.StructureViewExtension
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.structureView.PhpStructureViewTreeElement
import io.maliboot.devkit.idea.lombok.psi.PhpClassEx

class LombokStructureViewExtension : StructureViewExtension {
    override fun getType(): Class<out PsiElement> {
        return PhpClass::class.java
    }

    override fun getChildren(parent: PsiElement?): Array<StructureViewTreeElement> {
        if (parent !is PhpClass || DumbService.isDumb(parent.project)) {
            return arrayOf()
        }

        val result = mutableListOf<StructureViewTreeElement>()
        PhpClassEx(parent).let { phpClassEx ->
            phpClassEx.getFakeFields().takeIf { it.isNotEmpty() }?.forEach {
                result.add(PhpStructureViewTreeElement(it.value))
            }
            phpClassEx.getFakeMethods().takeIf { it.isNotEmpty() }?.forEach {
                result.add(PhpStructureViewTreeElement(it.value))
            }
        }

        return result.toTypedArray()
    }

    override fun getCurrentEditorElement(editor: Editor?, parent: PsiElement?): Any? {
        return null
    }
}