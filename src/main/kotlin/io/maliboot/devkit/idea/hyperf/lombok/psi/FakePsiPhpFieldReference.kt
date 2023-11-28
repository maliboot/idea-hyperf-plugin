package io.maliboot.devkit.idea.hyperf.lombok.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.FakePsiElement
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.resolve.PhpMemberResolveResult
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class FakePsiPhpFieldReference(val myFieldReference: FieldReference, val fakeField: FakePsiPhpField) : FakePsiElement(),
    FakePsiPhpClassMember, FieldReference {
    override fun getElement(): PsiElement {
        return myFieldReference.element
    }

    override fun getRangeInElement(): TextRange {
        return myFieldReference.rangeInElement
    }

    override fun resolve(): PsiElement {
        return fakeField
    }

    override fun getCanonicalText(): String {
        return myFieldReference.canonicalText
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return myFieldReference.handleElementRename(newElementName)
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return myFieldReference.bindToElement(element)
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return myFieldReference.isReferenceTo(element)
    }

    override fun isSoft(): Boolean {
        return myFieldReference.isSoft
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return arrayOf(PhpMemberResolveResult(fakeField, false))
    }

    override fun getParent(): PsiElement {
        return myFieldReference.parent
    }

    override fun getFirstPsiChild(): PhpPsiElement? {
        return myFieldReference.firstPsiChild
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return myFieldReference.nextPsiSibling
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        return myFieldReference.prevPsiSibling
    }

    override fun getType(): PhpType {
        return fakeField.type
    }

    override fun getFQN(): String? {
        return myFieldReference.fqn
    }

    override fun getImmediateNamespaceName(): String {
        return myFieldReference.immediateNamespaceName
    }

    override fun getNameNode(): ASTNode? {
        return myFieldReference.nameNode
    }

    override fun getNameCS(): CharSequence? {
        return myFieldReference.nameCS
    }

    override fun resolveLocal(): MutableCollection<out PhpNamedElement> {
        return myFieldReference.resolveLocal()
    }

    override fun resolveLocalType(): PhpType {
        return fakeField.type
    }

    override fun resolveGlobal(p0: Boolean): MutableCollection<out PhpNamedElement> {
        return mutableListOf(fakeField)
    }

    override fun getNamespaceName(): String {
        return myFieldReference.namespaceName
    }

    override fun isAbsolute(): Boolean {
        return myFieldReference.isAbsolute
    }

    override fun isStatic(): Boolean {
        return myFieldReference.isStatic
    }

    override fun getClassReference(): PhpExpression? {
        return myFieldReference.classReference
    }

    override fun getReferenceType(): PhpModifier.State {
        return myFieldReference.referenceType
    }

    override fun hasNullSafeDereference(): Boolean {
        return myFieldReference.hasNullSafeDereference()
    }

    override fun isWriteAccess(): Boolean {
        return myFieldReference.isWriteAccess
    }

    override fun isConstant(): Boolean {
        return myFieldReference.isConstant
    }

    override fun getReference(): PsiReference? {
        return myFieldReference.reference
    }
}