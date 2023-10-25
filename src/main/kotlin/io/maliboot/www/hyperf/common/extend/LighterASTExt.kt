package io.maliboot.www.hyperf.common.extend

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.psi.impl.source.tree.RecursiveLighterASTNodeWalkingVisitor
import com.intellij.psi.tree.IElementType
import com.intellij.ui.treeStructure.Tree
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes

fun LighterAST.getMethodLightASTNode(startOffset: Int): LighterASTNode? {
    var resultNode: LighterASTNode? = null
    object : RecursiveLighterASTNodeWalkingVisitor(this) {
        override fun visitNode(element: LighterASTNode) {
            if (element.tokenType != PhpElementTypes.CLASS_METHOD) {
                super.visitNode(element)
                return
            }
            if (!(element.startOffset <= startOffset && startOffset <= element.endOffset)) {
                super.visitNode(element)
                return
            }
            resultNode = element
        }
    }.visitNode(this.root)
    return resultNode
}

fun LighterAST.getMethodLightASTNode(methodName: String): LighterASTNode? {
    var resultNode: LighterASTNode? = null
    object : RecursiveLighterASTNodeWalkingVisitor(this) {
        override fun visitNode(element: LighterASTNode) {
            if (element.tokenType != PhpElementTypes.CLASS_METHOD) {
                super.visitNode(element)
                return
            }

            if (getNodeText(element) != methodName) {
                super.visitNode(element)
                return
            }
            resultNode = element
        }
    }.visitNode(this.root)
    return resultNode
}

fun LighterAST.findFirstChildLightASTNode(
    parentASTNode: LighterASTNode,
    startOffset: Int
): LighterASTNode? {
    if (startOffset < parentASTNode.startOffset) {
        return null
    }

    var resultNode: LighterASTNode? = null
    object : RecursiveLighterASTNodeWalkingVisitor(this) {
        override fun visitNode(element: LighterASTNode) {
            if (element.startOffset > startOffset) {
                resultNode = element
                super.stopWalking()
                return
            }
            super.visitNode(element)
        }
    }.visitNode(parentASTNode)
    return resultNode
}

fun LighterAST.getTreeIndexPath(
    parentASTNode: LighterASTNode,
    childASTNode: LighterASTNode,
    deepMax: Int = 500
): MutableList<Int>? {
    var count = deepMax
    var myChildNode = childASTNode

    val path: MutableList<Int> = mutableListOf()
    while (count >= 0) {
        val myParentNode = this.getParent(myChildNode) ?: return null
        // 追加path index
        path.add(0, this.getChildren(myParentNode).indexOf(myChildNode))
        // 遍历完成
        if (myParentNode == parentASTNode) {
            break
        }
        // 下一轮
        myChildNode = myParentNode
        count--
    }
    if (path.isEmpty()) {
        return null
    }
    return path
}

fun LighterAST.getChildByTreeIndexPath(parentASTNode: LighterASTNode, treeIndexPath: List<Int>): LighterASTNode? {
    var currentIndex = 0
    var myParentNode = parentASTNode

    while (currentIndex < treeIndexPath.size) {
        myParentNode = this.getChildren(myParentNode).takeIf {
            it.size > treeIndexPath[currentIndex]
        }?.get(treeIndexPath[currentIndex]) ?: return null
        currentIndex++
    }

    return myParentNode
}

fun LighterAST.getChildrenOfType(node: LighterASTNode, type: IElementType): LighterASTNode? {
    return LightTreeUtil.getChildrenOfType(this, node, type)
        .takeIf { it.isNotEmpty() }
        ?.first()
}

fun LighterAST.getNodeText(node: LighterASTNode, type: IElementType = PhpTokenTypes.IDENTIFIER): String? {
    return this.getChildrenOfType(node, type)?.let {
        LightTreeUtil.toFilteredString(this, it, null)
    }
}

fun LighterAST.getGroupStmtNode(parentASTNode: LighterASTNode): LighterASTNode? {
    return getChildrenOfType(parentASTNode, PhpElementTypes.GROUP_STATEMENT)
}

fun LighterAST.getHyperfProxyClosure(methodNode: LighterASTNode): LighterASTNode? {
    getChildrenOfType(methodNode, PhpElementTypes.GROUP_STATEMENT)?.let { groupStmtNode->
        getChildrenOfType(groupStmtNode, PhpElementTypes.RETURN)?.let { groupStmtReturnNode ->
            getChildrenOfType(groupStmtReturnNode, PhpElementTypes.METHOD_REFERENCE)?.takeIf {
                // 查找 hyperf -> self::__proxyCall
                getNodeText(it) == "__proxyCall"
            }
        }
    }?.let{ selfProxyCall ->
        getChildrenOfType(selfProxyCall, PhpElementTypes.PARAMETER_LIST)?.let { selfProxyCallParameterList ->
            getChildrenOfType(selfProxyCallParameterList, PhpElementTypes.CLOSURE)?.let { proxyClosure ->
                getChildrenOfType(proxyClosure, PhpElementTypes.FUNCTION)?.let {
                    return it
                }
            }
        }
    }
    return null
}