package io.maliboot.www.hyperf.debug

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.jetbrains.php.debug.template.PhpTemplateDebugStateService
import com.jetbrains.php.debug.template.PhpTemplateLanguagePathMapper
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import io.maliboot.www.hyperf.common.extend.*

class HyperfProxyPathMapper : PhpTemplateLanguagePathMapper() {

    override fun isTemplateFileType(p0: FileType): Boolean {
        return p0 is PhpFileType
    }

    /**
     * @param p0 1当前运行的文件/2打断点的当前文件
     */
    override fun isGeneratedFile(p0: VirtualFile, p1: Project): Boolean {
        // 1当前运行的文件
        if (p0.path.contains(p1.getHyperfProxyDir())) {
            return true
        }

        // 2打断点的当前文件：当前文件有代理文件时，需要将断点映射到代理文件->mapToPhp()方法
        if (p0.getHyperfProxyFile(p1) != null) {
            return true
        }

        return false
    }

    override fun mapToPhp(p0: XSourcePosition, p1: Project, p2: PhpPathMapper): XSourcePosition {
        // 当前运行的代理文件
        if (p0.file.path.contains(p1.getHyperfProxyDir())) {
            return p0
        }

        // 映射断点到代理文件
        return guessProxyXSourcePosition(p0, p1) ?: p0
    }

    override fun mapToTemplate(p0: XSourcePosition?, p1: Project, p2: PhpPathMapper): XSourcePosition? {
        if (p0 == null) {
            return null
        }
        if (p0.file.path.contains(p1.getHyperfProxyDir())) {
            return guessOriginXSourcePosition(p0, p1)
        }
        return p0
    }

    override fun getTemplateDebugInstance(p0: Project): PhpTemplateDebugStateService {
        val myState = PhpTemplateDebugStateService(p0)
        myState.cachePath = p0.getHyperfProxyDir()
        return myState
    }

    private fun guessProxyXSourcePosition(originPos: XSourcePosition, project: Project): XSourcePosition? {
        // 代理文件
        val proxyFile = originPos.file.getHyperfProxyFile(project) ?: return null

        // 原文件-获取类方法GroupStmt的开始行
        val originTree = originPos.file.findPsiFile(project)?.node?.lighterAST ?: return null
        val originMethodNode = originTree.getMethodLightASTNode(originPos.offset) ?: return null
        val classMethodName = originTree.getNodeText(originMethodNode) ?: return null
        val originFuncStartLine = originTree
            .getChildrenOfType(originMethodNode, PhpElementTypes.GROUP_STATEMENT)
            ?.getStartLineNumber(originPos.file)
            ?: return null

        // 代理文件位置
        val proxyTree = proxyFile.findPsiFile(project)?.node?.lighterAST ?: return null
        proxyTree.getMethodLightASTNode(classMethodName)?.let { proxyMethodNode ->
            // 排除AOP代码干扰
            proxyTree.getHyperfProxyClosure(proxyMethodNode) ?: proxyMethodNode
        }?.getStartLineNumber(proxyFile)?.let {
            it + (originPos.line - originFuncStartLine)
        }?.let {
            return XDebuggerUtil.getInstance().createPosition(proxyFile, it)
        }

        return null
    }

    private fun guessOriginXSourcePosition(proxyPos: XSourcePosition, project: Project): XSourcePosition? {
        // 获取原文件
        val originFile = proxyPos.file.getHyperfOriginFile(project) ?: return null

        // 代理文件-获取类方法[代理闭包]GroupStmt的开始行
        val proxyTree = proxyPos.file.findPsiFile(project)?.node?.lighterAST ?: return null
        val proxyMethodNode = proxyTree.getMethodLightASTNode(proxyPos.offset) ?: return null
        val classMethodName = proxyTree.getNodeText(proxyMethodNode) ?: return null
        val proxyFuncStartLine = proxyTree.getChildrenOfType(proxyMethodNode, PhpElementTypes.GROUP_STATEMENT)
            ?.getStartLineNumber(proxyPos.file)?.let {
                proxyTree.getHyperfProxyClosure(proxyMethodNode)?.getStartLineNumber(proxyPos.file) ?: it
            } ?: return null

        // 原文件位置
        val originTree = originFile.findPsiFile(project)?.node?.lighterAST ?: return null
        originTree.getMethodLightASTNode(classMethodName)?.let {
            originTree.getChildrenOfType(it, PhpElementTypes.GROUP_STATEMENT)
        }?.getStartLineNumber(originFile)?.let {
            it + (proxyPos.line - proxyFuncStartLine)
        }?.let {
            return XDebuggerUtil.getInstance().createPosition(originFile, it)
        }

        return null
    }
}