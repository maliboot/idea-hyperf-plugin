package io.maliboot.www.hyperf.lombok.codeInsight

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hints.declarative.InlayActionHandler
import com.intellij.codeInsight.hints.declarative.InlayActionPayload
import com.intellij.codeInsight.hints.declarative.PsiPointerInlayActionPayload
import com.intellij.openapi.editor.Editor
import io.maliboot.www.hyperf.lombok.psi.FakePsiPhpClassMember

class MethodDocInlayActionHandler: InlayActionHandler {
    override fun handleClick(editor: Editor, payload: InlayActionPayload) {
        payload as PsiPointerInlayActionPayload
        val fakePsiMember = payload.pointer.element as? FakePsiPhpClassMember ?: return
        HintManagerImpl.getInstanceImpl().showInformationHint(editor, fakePsiMember.getDocumentation())
    }
}