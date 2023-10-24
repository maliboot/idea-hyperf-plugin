package io.maliboot.www.hyperf.common.extend

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.psi.impl.source.tree.RecursiveLighterASTNodeWalkingVisitor
import com.intellij.psi.tree.IElementType
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

fun LighterAST.getHyperfProxyClosure(methodNode: LighterASTNode): LighterASTNode? {
    getChildrenOfType(methodNode, PhpElementTypes.GROUP_STATEMENT)?.let { groupStmtNode->
        getChildrenOfType(groupStmtNode, PhpElementTypes.RETURN)?.let { groupStmtReturnNode ->
            getChildrenOfType(groupStmtReturnNode, PhpElementTypes.METHOD_REFERENCE)?.takeIf {
                // 查找 hyperf -> self::__proxyCall
                getNodeText(it) == "__proxyCall"
            }?.let {
                return it
            }
        }
    }
    return null
}