package io.maliboot.devkit.idea.hyperf.lombok.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.util.Processor
import com.jetbrains.php.PhpClassMemberIconProvider
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.PhpNamedElementImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpPsiElementImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import io.maliboot.devkit.idea.hyperf.lombok.doc.LombokDocumentation
import io.maliboot.devkit.idea.hyperf.tool.ext.getPhpElementByFQN
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import javax.swing.Icon

class FakePsiPhpField(val field: CustomField, val phpClass: PhpClass, val generatedByFQN: String = "") :
    FakePsiElement(), FakePsiPhpClassMember, Field {

    private val myField: PhpPsiElement

    init {
        if (!field.modifier.isStatic) {
            myField = PhpPsiElementFactory.createClassField(
                project,
                field.modifier,
                field.name,
                field.defaultValue,
                field.returnType.toString()
            )
        } else {
            myField = PhpPsiElementFactory.createClassConstant(
                project,
                field.modifier,
                field.name,
                field.defaultValue ?: ""
            )
        }
    }

    override fun getIcon(): Icon {
        return PhpClassMemberIconProvider.getFieldIcon(this)
    }

    override fun getParent(): PsiElement {
        return containingClass
    }

    override fun getName(): String {
        return field.name
    }

    override fun getNameIdentifier(): PsiElement? {
        return PhpPsiElementFactory.createFromText(project, PhpTokenTypes.IDENTIFIER, name)
    }

    override fun getFirstPsiChild(): PhpPsiElement? {
        return myField.firstPsiChild
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getType(): PhpType {
        return field.returnType
    }

    override fun getNameNode(): ASTNode? {
        return myField.node
    }

    override fun getNameCS(): CharSequence {
        return field.name
    }

    override fun getDocComment(): PhpDocComment? {
        return null
    }

    override fun processDocs(p0: Processor<PhpDocComment>?) {

    }

    override fun getFQN(): String {
        return "${phpClass.fqn}.$name)"
    }

    override fun getNamespaceName(): String {
        return PhpNamedElementImpl.getNamespace(this)
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override fun isInternal(): Boolean {
        return !field.modifier.isPublic
    }

    override fun getModifier(): PhpModifier {
        return field.modifier
    }

    override fun getContainingClass(): PhpClass {
        return phpClass
    }

    override fun isWriteAccess(): Boolean {
        return true
    }

    override fun getTypeDeclaration(): PhpTypeDeclaration {
        return PhpPsiElementFactory.createReturnType(project, field.returnType.toString())
    }

    override fun getAttributes(): MutableCollection<PhpAttribute> {
        return mutableListOf()
    }

    override fun isConstant(): Boolean {
        return field.modifier.isStatic
    }

    override fun isReadonly(): Boolean {
        return true
    }

    override fun getDefaultValue(): PsiElement? {
        val defaultStr = field.defaultValue ?: return null
        return PhpPsiElementFactory.createStringLiteralExpression(project, defaultStr, false)
    }

    override fun getDefaultValuePresentation(): String? {
        return field.defaultValue
    }

    override fun getParentList(): PhpClassFieldsList? {
        var phpClassFieldsList: PhpClassFieldsList? = null
        myField.accept(object : PhpElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is PhpClassFieldsList) {
                    phpClassFieldsList = element
                }
            }
        })
        return phpClassFieldsList
    }

    override fun toString(): String {
        return PhpPsiElementImpl.toStringWithValue(this, this.name)
    }

    override fun getText(): String {
        val myText = "$.'$'$name"
        if (defaultValue == null) {
            return myText
        }

        return myText + "=${defaultValue!!.text}"
    }

    override fun getGeneratedElement(): PhpNamedElement {
        return generatedByFQN.getPhpElementByFQN(phpClass.project, phpClass) ?: phpClass
    }

    override fun getDocumentation(): String {
        return LombokDocumentation().myGenerateDoc(this, null) ?: ""
    }

    override fun getFeatureName(): String {
        return field.feature
    }
}