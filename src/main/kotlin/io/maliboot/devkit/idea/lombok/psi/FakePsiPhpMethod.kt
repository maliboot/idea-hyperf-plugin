package io.maliboot.devkit.idea.lombok.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.jetbrains.php.PhpPresentationUtil
import com.jetbrains.php.codeInsight.PhpScope
import com.jetbrains.php.codeInsight.controlFlow.PhpControlFlow
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpExpressionCodeFragmentImpl
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.lombok.doc.LombokDocumentation
import io.maliboot.devkit.idea.lombok.extend.getPhpElementByFQN
import javax.swing.Icon


class FakePsiPhpMethod(val method: CustomMethod, val phpClass: PhpClass, val generatedByFQN: String = "") :
    FakePsiElement(), FakePsiPhpClassMember, Method {

    private val scope = PhpExpressionCodeFragmentImpl.CodeFragmentEmptyScope("LombokCustomException")

    var isRealParameter = false

    override fun getPresentation(): ItemPresentation? {
        return PhpPresentationUtil.getMethodPresentation(this)
    }

    override fun getIcon(): Icon {
        return MethodImpl.getIcon(this)
    }

    override fun getParent(): PsiElement {
        return containingClass
    }

    override fun getName(): String {
        return method.name
    }

    override fun getNameIdentifier(): PsiElement {
        return PhpPsiElementFactory.createFromText(project, PhpTokenTypes.IDENTIFIER, method.name)
    }

    override fun getFirstPsiChild(): PhpPsiElement {
        return firstChild as PhpPsiElement
    }

    override fun getChildren(): Array<PsiElement> {
        return PhpPsiElementFactory.createMethod(
            project, """
            $modifier function $name(${method.parametersAsString(project)}): ${typeDeclaration.text}
        """.trimIndent()
        ).children
//        return arrayOf(
//            PhpPsiElementFactory.createModifierList(project, method.modifier),
//            PhpPsiElementFactory.createFromText(project, PhpTokenTypes.kwFUNCTION, "function"),
//            nameIdentifier,
//        )
    }

    override fun getFirstChild(): PsiElement {
        return children.first()
    }

    override fun getLastChild(): PsiElement {
        return children.last()
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        // PhpDocCommentImpl(elementType=PhpDocCommentElementType,text=[php注释])
        return null
    }

    override fun getType(): PhpType {
        return method.returnType
    }

    override fun getNameNode(): ASTNode? {
        return null
    }

    override fun getNameCS(): CharSequence {
        return method.name
    }

    override fun getDocComment(): PhpDocComment? {
        return PhpPsiElementFactory.createFromText(
            project,
            PhpDocComment::class.java,
            """
/**
 * @link     https://github.com/maliboot/lombok
 * @document https://github.com/maliboot/lombok
 * @author   maliboot
 * @contact  group@maliboot.io
 */
            """.trimIndent(),
        )
    }

    override fun processDocs(processor: Processor<PhpDocComment>) {
        processor.process(docComment)
    }

    override fun getFQN(): String {
        val phpClass = this.containingClass

        return "${phpClass.fqn}.${method.name}"
    }

    override fun getNamespaceName(): String {
        return this.containingClass.namespaceName
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override fun isInternal(): Boolean {
        return false
    }

    override fun getModifier(): PhpModifier {
        return method.modifier
    }

    override fun getContainingClass(): PhpClass {
        return phpClass
    }

    override fun getControlFlow(): PhpControlFlow {
        return scope.controlFlow
    }

    override fun getPredefinedVariables(): MutableSet<CharSequence> {
        return mutableSetOf()
    }

    override fun getScope(): PhpScope {
        return scope
    }

    override fun getTypeDeclaration(): PhpReturnType {
        return PhpPsiElementFactory.createReturnType(project, method.returnType.toString())
    }

    override fun getAttributes(): MutableCollection<PhpAttribute> {
        return mutableListOf()
    }

    override fun getParameters(): Array<out Parameter> {
        if (isRealParameter) {
            return method.parameters
                .map {
                    PhpPsiElementFactory.createComplexParameter(project, it.asText())
                }
                .toTypedArray()
        }

        // 把 findChildOfType 包装进 ReadAction
        return ReadAction.compute<Collection<Parameter>?, Throwable> {
            PsiTreeUtil.findChildrenOfType(children.first { it is ParameterList }, Parameter::class.java)
        }?.toTypedArray() ?: emptyArray()
    }

    override fun getParameter(index: Int): Parameter? {
        if (parameters.size <= index) {
            return null
        }

        return parameters[index]
    }

    override fun hasRefParams(): Boolean {
        return this.parameters.any { it.isPassByRef }
    }

    override fun isClosure(): Boolean {
        return false
    }

    override fun getLocalType(interactive: Boolean): PhpType {
        return type
    }

    @Deprecated("Deprecated in Java", ReplaceWith("typeDeclaration"))
    override fun getReturnType(): PhpReturnType {
        return typeDeclaration
    }

    @Deprecated("Deprecated in Java", ReplaceWith("mutableListOf()"))
    override fun getDocExceptions(): MutableCollection<String> {
        return mutableListOf()
    }

    override fun getMethodType(allowAmbiguity: Boolean): Method.MethodType {
        return Method.MethodType.REGULAR_METHOD
    }

    override fun isStatic(): Boolean {
        return method.modifier.isStatic
    }

    override fun isFinal(): Boolean {
        return method.modifier.isFinal
    }

    override fun isAbstract(): Boolean {
        return method.modifier.isAbstract
    }

    override fun getAccess(): PhpModifier.Access {
        return method.modifier.access
    }

    override fun toString(): String {
        return "FakePsiPhpMethod(method=$method, phpClass=$phpClass, scope=$scope)"
    }

    override fun getDocumentation(): String {
        return LombokDocumentation().myGenerateDoc(this, null) ?: ""
    }

    override fun getGeneratedElement(): PhpNamedElement {
        return generatedByFQN.getPhpElementByFQN(phpClass.project, phpClass) ?: phpClass
    }

    override fun getFeatureName(): String {
        return method.feature
    }

    override fun getLanguage(): Language {
        return PhpLanguage.INSTANCE
    }
}