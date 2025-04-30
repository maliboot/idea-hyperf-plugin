package io.maliboot.devkit.idea.lombok.codeInsight

import com.intellij.codeInsight.hints.declarative.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.startOffset
import com.jetbrains.php.lang.psi.elements.PhpClass
import io.maliboot.devkit.idea.lombok.psi.FakePsiPhpMethod
import io.maliboot.devkit.idea.lombok.psi.PhpClassEx

class MethodInlayProvider : InlayHintsProvider {

    override fun createCollector(
        file: PsiFile,
        editor: Editor
    ): InlayHintsCollector {
        return Collector()
    }

    private class Collector : SharedBypassCollector {
        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            if (element !is PhpClass) {
                return
            }
            val phpClassEx = PhpClassEx(element)

            // 自定义方法内嵌提示
            for (field in element.fields) {
                for (methodEntry in phpClassEx.getFakeMethods().entries) {
                    val fakeMethodEntry = methodEntry.value as FakePsiPhpMethod
                    if (fakeMethodEntry.generatedByFQN != field.fqn) {
                        continue
                    }

                    sink.addPresentation(
                        position = InlineInlayPosition(field.startOffset  + field.textLength + 1, true),
                        tooltip = fakeMethodEntry.getDocumentation(),
                        hintFormat = HintFormat.default.withHorizontalMargin(HintFormat.default.horizontalMarginPadding)
                    ) {
                        text(
                            fakeMethodEntry.getFeatureName(),
                            InlayActionData(
                                PsiPointerInlayActionPayload(
                                    SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(fakeMethodEntry)
                                ),
                                PsiPointerInlayActionNavigationHandler.HANDLER_ID
                            )
                        )
                    }
                }
            }
        }
    }
}