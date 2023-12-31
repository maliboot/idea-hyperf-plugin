package io.maliboot.devkit.idea.hyperf.lombok.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.Processor
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag
import com.jetbrains.php.lang.parser.PhpStubElementTypes
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.PhpNamedElementImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.stubs.PhpParameterStub
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomParameter
import javax.swing.Icon

class FakePsiPhpParameter(private val parameter: CustomParameter, val method: FakePsiPhpMethod) : FakePsiElement(),
    Parameter {

    override fun getIcon(): Icon {
        return PhpIcons.PARAMETER
    }

    override fun getParent(): PsiElement {
        return method
    }

    override fun getName(): String {
        return parameter.name
    }

    override fun getNameIdentifier(): PsiElement? {
        return PhpPsiElementFactory.createNamedArgumentNameIdentifier(project, name)
    }

    override fun getFirstPsiChild(): PhpPsiElement? {
        return null
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getType(): PhpType {
        return parameter.returnType
    }

    override fun getDeclaredType(): PhpType {
        return parameter.returnType
    }

    override fun getNameNode(): ASTNode? {
        return null
    }

    override fun getNameCS(): CharSequence {
        return parameter.name
    }

    override fun getDocComment(): PhpDocComment? {
        return null
    }

    override fun processDocs(processor: Processor<PhpDocComment>?) {
    }

    override fun getFQN(): String {
        return "${method.fqn}($name)"
    }

    override fun getNamespaceName(): String {
        return PhpNamedElementImpl.getNamespace(this)
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override fun isInternal(): Boolean {
        return false
    }

    override fun isWriteAccess(): Boolean {
        return true
    }

    override fun getElementType(): IStubElementType<out StubElement<*>, *> {
        return PhpStubElementTypes.NOT_PROMOTED_PARAMETER
    }

    override fun getStub(): PhpParameterStub? {
        return null
    }

    override fun getTypeDeclaration(): PhpTypeDeclaration {
        return PhpPsiElementFactory.createReturnType(project, declaredType.toString())
    }

    override fun getAttributes(): MutableCollection<PhpAttribute> {
        return mutableListOf()
    }

    override fun isOptional(): Boolean {
        return parameter.defaultValue != null
    }

    @Suppress("UnstableApiUsage")
    override fun isOptional(p0: MutableList<out PsiElement>?): Boolean {
        return isOptional
    }

    override fun isVariadic(): Boolean {
        return false
    }

    override fun getLocalType(): PhpType {
        return parameter.returnType
    }

    override fun getDefaultValue(): PsiElement? {
        val defaultStr = parameter.defaultValue ?: return null
        return PhpPsiElementFactory.createStringLiteralExpression(project, defaultStr, false)
    }

    override fun getDefaultValuePresentation(): String? {
        return parameter.defaultValue
    }

    override fun isPassByRef(): Boolean {
        return false
    }

    override fun getDocTag(): PhpDocParamTag? {
        return null
    }

    override fun isPromotedField(): Boolean {
        return false
    }

    override fun getPromotedFieldAccess(): PhpModifier.Access {
        return PhpModifier.Access.PUBLIC
    }

    override fun getText(): String {
        return parameter.name
    }
}