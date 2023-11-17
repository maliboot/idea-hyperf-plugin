package io.maliboot.www.hyperf.lombok.reference

import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull

// todo: not use => to ReferenceSearch
class LombokReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val match = object : PsiElementPattern.Capture<PsiElement>(PsiElement::class.java) {
            override fun accepts(o: Any?, context: ProcessingContext?): Boolean {
                return super.accepts(o, context)
            }
        }
//            .withParent(
//                PlatformPatterns
//                    .psiElement(ParameterList::class.java)
//                    .withParent(
//                        PhpPatterns
//                            .phpFunctionReference()
//                    )
//            ).withLanguage(PhpLanguage.INSTANCE)
        registrar.registerReferenceProvider(
            match,
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    @NotNull element: PsiElement,
                    @NotNull context: ProcessingContext
                ): Array<out PsiReference> {
                    return PsiReference.EMPTY_ARRAY
                }

            })
    }
}