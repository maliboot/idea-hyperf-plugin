package io.maliboot.devkit.idea.hyperf

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import io.maliboot.devkit.idea.common.extend.*

object HyperfProxyFileUtil {
    var templateDir = arrayListOf("app")
    var proxyDir = "runtime/container"

    fun guessProxyXSourcePosition(originPos: XSourcePosition, project: Project): XSourcePosition? {
        // 代理文件
        val proxyFile = originPos.file.getHyperfProxyFile(project) ?: return null

        // 模板文件
        val originTree = originPos.file.findPsiFile(project)?.node?.lighterAST ?: return null
        val originMethodNode = originTree.getMethodLightASTNode(originPos.offset) ?: return null
        val classMethodName = originTree.getNodeText(originMethodNode) ?: return null
        // 获取方法与断点之间的treePath
        val sourcePosRelativeTreePath =
            originTree.getGroupStmtNode(originMethodNode)?.let { originMethodGroupStmtNode ->
                originTree.findFirstChildLightASTNode(originMethodGroupStmtNode, originPos.offset)?.let {
                    originTree.getTreeIndexPath(originMethodGroupStmtNode, it)
                }
            } ?: return null

        // 代理文件位置
        val proxyTree = proxyFile.findPsiFile(project)?.node?.lighterAST ?: return null
        proxyTree.getMethodLightASTNode(classMethodName)?.let { proxyMethodNode ->
            // 排除AOP代码干扰
            proxyTree.getHyperfProxyClosure(proxyMethodNode) ?: proxyMethodNode
        }?.let {
            proxyTree.getGroupStmtNode(it)
        }?.let {
            proxyTree.getChildByTreeIndexPath(it, sourcePosRelativeTreePath)
        }?.let {
            return XDebuggerUtil.getInstance().createPositionByOffset(proxyFile, it.startOffset)
        }

        return null
    }

    fun guessOriginXSourcePosition(proxyPos: XSourcePosition, project: Project): XSourcePosition? {
        // 获取模板
        val originFile = proxyPos.file.getHyperfOriginFile(project) ?: return null

        // 代理文件-获取类方法[代理闭包]GroupStmt的开始行
        val proxyTree = proxyPos.file.findPsiFile(project)?.node?.lighterAST ?: return null
        val proxyMethodNode = proxyTree.getMethodLightASTNode(proxyPos.offset) ?: return null
        val classMethodName = proxyTree.getNodeText(proxyMethodNode) ?: return null
        // 获取方法与断点之间的treePath
        val sourcePosRelativeTreePath = (proxyTree.getHyperfProxyClosure(proxyMethodNode) ?: proxyMethodNode).let {
            proxyTree.getGroupStmtNode(it)
        }?.let { funcNode ->
            proxyTree.findFirstChildLightASTNode(funcNode, proxyPos.offset)?.let {
                proxyTree.getTreeIndexPath(funcNode, it)
            }
        } ?: return null

        // 模板位置
        val originTree = originFile.findPsiFile(project)?.node?.lighterAST ?: return null
        originTree.getMethodLightASTNode(classMethodName)?.let {
            originTree.getGroupStmtNode(it)
        }?.let {
            originTree.getChildByTreeIndexPath(it, sourcePosRelativeTreePath)
        }?.let {
            return XDebuggerUtil.getInstance().createPositionByOffset(originFile, it.startOffset)
        }

        return null
    }
}