package io.maliboot.devkit.idea.lombok.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.FakePsiElement
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.resolve.PhpMemberResolveResult
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class FakePsiPhpMethodReference(val myMethodReference: MethodReference, val fakeMethod: FakePsiPhpMethod) :
    FakePsiElement(), FakePsiPhpClassMember, MethodReference {
    init {
        fakeMethod.isRealParameter = true
    }

    override fun getElement(): PsiElement {
        return myMethodReference.element
    }

    override fun getRangeInElement(): TextRange {
        return myMethodReference.rangeInElement
    }

    override fun resolve(): PsiElement {
        return fakeMethod
    }

    override fun getCanonicalText(): String {
        return myMethodReference.canonicalText
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return myMethodReference.handleElementRename(newElementName)
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return myMethodReference.bindToElement(element)
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return myMethodReference.isReferenceTo(element)
    }

    override fun isSoft(): Boolean {
        return myMethodReference.isSoft
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return arrayOf(PhpMemberResolveResult(fakeMethod, false))
    }

    override fun getParent(): PsiElement {
        return myMethodReference.parent
    }

    override fun getFirstPsiChild(): PhpPsiElement? {
        return myMethodReference.firstPsiChild
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return myMethodReference.nextPsiSibling
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        return myMethodReference.prevPsiSibling
    }

    override fun getType(): PhpType {
        return fakeMethod.type
    }

    override fun getFQN(): String? {
        return myMethodReference.fqn
    }

    override fun getImmediateNamespaceName(): String {
        return myMethodReference.immediateNamespaceName
    }

    override fun getNameNode(): ASTNode? {
        return myMethodReference.nameNode
    }

    override fun getNameCS(): CharSequence? {
        return myMethodReference.nameCS
    }

    override fun resolveLocal(): MutableCollection<out PhpNamedElement> {
        return myMethodReference.resolveLocal()
    }

    override fun resolveLocalType(): PhpType {
        return fakeMethod.type
    }

    override fun resolveGlobal(p0: Boolean): MutableCollection<out PhpNamedElement> {
        return mutableListOf(fakeMethod)
    }

    override fun getNamespaceName(): String {
        return myMethodReference.namespaceName
    }

    override fun isAbsolute(): Boolean {
        return myMethodReference.isAbsolute
    }

    override fun isStatic(): Boolean {
        return myMethodReference.isStatic
    }

    override fun getClassReference(): PhpExpression? {
        return myMethodReference.classReference
    }

    override fun getReferenceType(): PhpModifier.State {
        return myMethodReference.referenceType
    }

    override fun hasNullSafeDereference(): Boolean {
        return myMethodReference.hasNullSafeDereference()
    }

    override fun getParameterList(): ParameterList? {
        return myMethodReference.parameterList
    }

    override fun getParameters(): Array<PsiElement> {
        return myMethodReference.parameters
    }

    override fun getReference(): PsiReference? {
        return myMethodReference.reference
    }
}