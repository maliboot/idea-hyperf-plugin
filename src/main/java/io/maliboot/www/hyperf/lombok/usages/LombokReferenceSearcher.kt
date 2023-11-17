package io.maliboot.www.hyperf.lombok.usages

//import com.intellij.psi.PsiClass
//import com.intellij.psi.PsiField
//import de.plushnikov.intellij.plugin.psi.LombokLightFieldBuilder
//import de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder
import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchRequestCollector
import com.intellij.psi.search.UsageSearchContext
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpCallableMethod
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

// todo: not use => to ReferenceSearch
class LombokReferenceSearcher :
    QueryExecutorBase<PsiReference?, ReferencesSearch.SearchParameters>(true) {
    override fun processQuery(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference?>
    ) {
        val refElement = queryParameters.elementToSearch
        if (refElement is Field) {
            DumbService.getInstance(queryParameters.project).runReadActionInSmartMode {
                processPsiField(refElement, queryParameters.optimizer)
            }
        }

        if (refElement is Method) {
            DumbService.getInstance(queryParameters.project).runReadActionInSmartMode {
                processClassMethods(refElement, queryParameters.optimizer)
            }
        }

        refElement.containingFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                element.accept(PhpElVisitor())
            }

            private inner class PhpElVisitor : PhpElementVisitor() {


                override fun visitPhpMethodReference(reference: MethodReference?) {
                    super.visitPhpMethodReference(reference)
                }

                override fun visitPhpCallableMethod(reference: PhpCallableMethod?) {
                    super.visitPhpCallableMethod(reference)
                }
            }
        })
    }

    private fun processPsiField(refPsiField: Field, collector: SearchRequestCollector) {
        val containingClass = refPsiField.containingClass
        if (null != containingClass) {
//            processClassMethods(refPsiField, collector, containingClass)
        }
    }

    private fun processClassMethods(
        refPsiMethod: Method,
        collector: SearchRequestCollector,
    ) {
        collector.searchWord(
            refPsiMethod.name,
            refPsiMethod.useScope,
            UsageSearchContext.IN_CODE,
            true,
            refPsiMethod
        )
//        containingClass.methods
//            .filter { obj: Any? -> LombokLightMethodBuilder::class.java.isInstance(obj) }
//            .filter { psiMethod ->
//                psiMethod.navigationElement === refPsiMethod
//            }
//            .forEach { psiMethod ->
//                collector.searchWord(
//                    psiMethod.name,
//                    psiMethod.useScope,
//                    UsageSearchContext.IN_CODE,
//                    true,
//                    refPsiMethod
//                )
//            }
    }

//    private fun processClassFields(
//        refPsiField: PsiField,
//        collector: SearchRequestCollector,
//        containingClass: PsiClass
//    ) {
//        Arrays.stream(containingClass.getFields())
//            .filter { obj: Any? -> LombokLightFieldBuilder::class.java.isInstance(obj) }
//            .filter { psiField -> psiField.getNavigationElement() === refPsiField }
//            .filter { psiField -> Objects.nonNull(psiField.getName()) }
//            .forEach { psiField ->
//                collector.searchWord(
//                    psiField.getName(),
//                    psiField.getUseScope(),
//                    UsageSearchContext.IN_CODE,
//                    true,
//                    psiField
//                )
//            }
//    }
}

