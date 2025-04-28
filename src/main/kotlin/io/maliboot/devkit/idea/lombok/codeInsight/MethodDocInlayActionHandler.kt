package io.maliboot.devkit.idea.lombok.codeInsight

import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hints.declarative.InlayActionHandler
import com.intellij.codeInsight.hints.declarative.InlayActionPayload
import com.intellij.codeInsight.hints.declarative.PsiPointerInlayActionPayload
import com.intellij.openapi.editor.event.EditorMouseEvent
import io.maliboot.devkit.idea.lombok.psi.FakePsiPhpClassMember

class MethodDocInlayActionHandler: InlayActionHandler {
    override fun handleClick(
        e: EditorMouseEvent,
        payload: InlayActionPayload
    ) {
        payload as PsiPointerInlayActionPayload
        val fakePsiMember = payload.pointer.element as? FakePsiPhpClassMember ?: return
        HintManagerImpl.getInstanceImpl().showInformationHint(e.editor, fakePsiMember.getDocumentation())
    }
}