package io.maliboot.www.hyperf.lombok.index

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.utils.vfs.getPsiFile
import com.intellij.util.gist.GistManager
import com.intellij.util.gist.PsiFileGist
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.PhpFile
import io.maliboot.www.hyperf.common.psi.data.CustomClass
import io.maliboot.www.hyperf.common.psi.data.MyCustomMember
import io.maliboot.www.hyperf.lombok.annotation.LombokAnnotationFactory
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpAttribute
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpFieldNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpMethodNode
import io.maliboot.www.hyperf.lombok.extend.realPath
import io.maliboot.www.hyperf.lombok.index.externalizer.ClassMemberDataExternalizer
import io.maliboot.www.hyperf.lombok.index.externalizer.MapDataExternalizer

class CustomMemberPsiGist {
    companion object {
        private const val ID = "php.maliboot.lombok.custom.members"

        private val logger: Logger = Logger.getInstance(CustomMemberPsiGist::class.java)

        private var phpClassCustomMembersGist: PsiFileGist<Map<String, MyCustomMember>> =
            GistManager.getInstance().newPsiFileGist(
                ID,
                getVersion(),
                getValueExternalizer()
            ) { file -> getCalcData(file) }

        private fun resolveFromIndexValue(psiFile: PsiFile): Map<String, MyCustomMember> {
            val bathPath = psiFile.project.basePath ?: return emptyMap()
            if (!psiFile.realPath.contains(bathPath, false)) return emptyMap()
            val excludeList = listOf("bin", "config", "runtime", "vendor")
            for (exclude in excludeList) {
                if (psiFile.realPath.contains("$bathPath/$exclude", false)) return emptyMap()
            }
            if (psiFile !is PhpFile) {
                return emptyMap()
            }
            return phpClassCustomMembersGist.getFileData(psiFile)
        }

        private fun getCalcData(psiFile: PsiFile): Map<String, MyCustomMember> {
            return findCandidates(psiFile.node.lighterAST)
        }

        private fun findCandidates(tree: LighterAST): Map<String, MyCustomMember> {
            // 获取psi-phpNamespaceImpl
            val phpNamespaceImplNode =
                getChildrenOfType(tree, tree.root, PhpElementTypes.NON_LAZY_GROUP_STATEMENT)?.let {
                    LightTreeUtil.getChildrenOfType(tree, it, PhpElementTypes.NAMESPACE).first()
                } ?: return emptyMap()
            val phpNamespaceImplNodeText = getNodeText(tree, phpNamespaceImplNode)?.let { nsName ->
                getNodeText(tree, phpNamespaceImplNode, PhpElementTypes.NS_REFERENCE)?.let { it + nsName } ?: nsName
            } ?: ""

            // 获取psi-phpNamespaceImpl(nonLazyGroupStatement)
            val phpNamespaceImplNonLazyGroupStmt =
                getChildrenOfType(tree, phpNamespaceImplNode, PhpElementTypes.NON_LAZY_GROUP_STATEMENT)
                    ?: return emptyMap()

            // 获取所有uses
            val allLombokAnnotationPClasses = LombokAnnotationFactory.getAnnotationPClasses()
            val uses = LightTreeUtil.getChildrenOfType(tree, phpNamespaceImplNonLazyGroupStmt, PhpElementTypes.USE_LIST)
                .map { useListNode ->
                    getChildrenOfType(tree, useListNode, PhpElementTypes.USE)?.let {
                        "\\" + LightTreeUtil.toFilteredString(tree, it, null)
                    } ?: ""
                }
                .filter {
                    it != "" && allLombokAnnotationPClasses.contains(it)
                }.associateBy { it.split("\\").last() }.ifEmpty {
                    return emptyMap()
                }

            // 获取psi-phpClassNode
            val phpClassNode =
                getChildrenOfType(tree, phpNamespaceImplNonLazyGroupStmt, PhpElementTypes.CLASS) ?: return emptyMap()
            val phpClassNodeText = getNodeText(tree, phpClassNode) ?: return emptyMap()
            val phpClassFqn = "\\$phpNamespaceImplNodeText\\$phpClassNodeText"

            val myPhpClassAttributeList: MutableList<PhpAttribute> = mutableListOf()
            val myPhpClassFieldList: MutableList<PhpFieldNode> = mutableListOf()
            val myPhpClassMethodList: MutableList<PhpMethodNode> = mutableListOf()
            for (child in tree.getChildren(phpClassNode)) {
                when (child.tokenType) {
                    PhpElementTypes.ATTRIBUTES_LIST -> getAttribute(
                        tree,
                        child,
                        uses
                    )?.let { myPhpClassAttributeList.add(it) }

                    PhpElementTypes.CLASS_FIELDS -> getField(tree, child, uses)?.let { myPhpClassFieldList.add(it) }
                    PhpElementTypes.CLASS_METHOD -> getMethod(tree, child)?.let { myPhpClassMethodList.add(it) }
                }
            }
            LombokAnnotationFactory.getMyCustomMembers(
                PhpClassNode(
                    phpClassNodeText,
                    phpClassFqn,
                    uses,
                    myPhpClassFieldList,
                    myPhpClassMethodList,
                    myPhpClassAttributeList
                )
            ).takeIf { it.isNotEmpty() }
                ?.let {
                    it[phpClassFqn] = MyCustomMember(phpClassFqn, "", CustomClass(phpClassFqn))
                    return it
                }

            return emptyMap()
        }

        private fun getAttribute(
            tree: LighterAST,
            phpAttributeListImpl: LighterASTNode,
            uses: Map<String, String>
        ): PhpAttribute? {
            val attrList = tree.getChildren(phpAttributeListImpl)
            if (attrList.last().tokenType != PhpTokenTypes.chRBRACKET) {
                return null
            }
            var phpAttributeNode: LighterASTNode? = null
            for (attr in attrList) {
                if (attr.tokenType == PhpElementTypes.ATTRIBUTE) {
                    phpAttributeNode = attr
                    break
                }
            }
            if (phpAttributeNode == null) {
                return null
            }

            val phpAttributeNodeText =
                getNodeText(tree, phpAttributeNode, PhpElementTypes.CLASS_REFERENCE) ?: return null
            val phpAttributeParams = getChildrenOfType(tree, phpAttributeNode, PhpElementTypes.PARAMETER_LIST)
                ?.let { paramListNode ->
                    LightTreeUtil.toFilteredString(tree, paramListNode, null).split(",").map { paramStr ->
                        filterClassType(paramStr, uses)
                    }
                } ?: emptyList()

            return PhpAttribute(
                phpAttributeNodeText,
                uses[phpAttributeNodeText] ?: phpAttributeNodeText,
                phpAttributeParams
            )
        }

        private fun getField(
            tree: LighterAST,
            phpPsiElementImpl: LighterASTNode,
            uses: Map<String, String>
        ): PhpFieldNode? {
            val myFieldChildren = tree.getChildren(phpPsiElementImpl)
            if (myFieldChildren.last().tokenType != PhpTokenTypes.opSEMICOLON) {
                return null
            }
            val myFieldAttrs: MutableList<PhpAttribute> = mutableListOf()
            var myFieldName: String? = null
            var myFieldDefault: String? = null
            val myFieldTypeList: MutableSet<String> = mutableSetOf()
            for (child in myFieldChildren) {
                when (child.tokenType) {
                    PhpElementTypes.ATTRIBUTES_LIST -> getAttribute(tree, child, uses)?.let { myFieldAttrs.add(it) }
                    PhpElementTypes.CLASS_FIELD -> {
                        LightTreeUtil.toFilteredString(tree, child, null).removePrefix("$").split("=").let {
                            myFieldName = it[0].trim()
                            if (it.size == 2) {
                                myFieldDefault = filterClassType(it[1], uses)
                            }
                        }
                    }

                    PhpElementTypes.FIELD_TYPE -> {
                        LightTreeUtil.toFilteredString(tree, child, null).split("|").forEach {
                            if (it.contains("?")) {
                                myFieldTypeList.add("null")
                            }
                            myFieldTypeList.add(filterClassType(it, uses))
                        }
                    }
                }
            }
            if (myFieldName == null || myFieldTypeList.isEmpty()) {
                return null
            }
            return PhpFieldNode(myFieldName!!, myFieldTypeList.joinToString("|"), myFieldDefault, myFieldAttrs)
        }

        private fun filterClassType(type: String, uses: Map<String, String>): String {
            return if (type.contains("::class")) {
                type.substringAfter(":").trim().removeSuffix("::class").let {
                    uses[it]?.let { it1 -> type.replace(it, it1, true) } ?: type
                }.trim()
            } else {
                type.trim()
            }
        }

        private fun getMethod(tree: LighterAST, methodImpl: LighterASTNode): PhpMethodNode? {
            return getNodeText(tree, methodImpl)?.let { PhpMethodNode(it) }
        }

        private fun getChildrenOfType(tree: LighterAST, node: LighterASTNode, type: IElementType): LighterASTNode? {
            return LightTreeUtil.getChildrenOfType(tree, node, type)
                .takeIf { it.isNotEmpty() }
                ?.first()
        }

        private fun getNodeText(
            tree: LighterAST,
            node: LighterASTNode,
            type: IElementType = PhpTokenTypes.IDENTIFIER
        ): String? {
            return getChildrenOfType(tree, node, type)?.let {
                LightTreeUtil.toFilteredString(tree, it, null)
            }
        }

        private fun getValueExternalizer(): DataExternalizer<Map<String, MyCustomMember>> {
            return MapDataExternalizer(EnumeratorStringDescriptor.INSTANCE, ClassMemberDataExternalizer.INSTANCE)
        }

        private fun getVersion(): Int {
            return 0
        }

        fun getCustomMembers(psiFile: PsiFile): Map<String, MyCustomMember> {
            return resolveFromIndexValue(psiFile)
        }

        fun getCustomMembers(file: VirtualFile, project: Project): Map<String, MyCustomMember> {
            return getCustomMembers(file.getPsiFile(project))
        }
    }
}