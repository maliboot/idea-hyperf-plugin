package io.maliboot.devkit.idea.hyperf.debug

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import io.maliboot.devkit.idea.hyperf.HyperfProxyFileUtil

class HyperfBreakpointListener(private val project: Project) : XBreakpointListener<XLineBreakpoint<*>> {

    override fun breakpointAdded(breakpoint: XLineBreakpoint<*>) {
        remapBreakpoint(breakpoint)
    }

    override fun breakpointChanged(breakpoint: XLineBreakpoint<*>) {
        remapBreakpoint(breakpoint)
    }

    override fun breakpointRemoved(breakpoint: XLineBreakpoint<*>) {
        remapBreakpoint(breakpoint, true)
    }

    private fun remapBreakpoint(breakpoint: XLineBreakpoint<*>, isRemoved: Boolean = false) {
        val file = breakpoint.fileUrl ?: return
        // 过滤目录
        val filterPathList = HyperfProxyFileUtil.templateDir + arrayListOf(HyperfProxyFileUtil.proxyDir)
        var isValidFile = false
        for (filterPath in filterPathList) {
            if (file.contains(filterPath)) {
                isValidFile = true
                break
            }
        }
        if (!isValidFile) {
            return
        }

        val targetFileXPosition = if (file.contains(".proxy.php")) {
            HyperfProxyFileUtil.guessOriginXSourcePosition(breakpoint.sourcePosition!!, project) ?: return
        } else {
            HyperfProxyFileUtil.guessProxyXSourcePosition(breakpoint.sourcePosition!!, project) ?: return
        }

        ApplicationManager.getApplication().invokeLater {
            ApplicationManager.getApplication().runWriteAction {
                val breakpointManager = XDebuggerManager.getInstance(project).breakpointManager

                val existingTargetBreakPoint = breakpointManager.getBreakpoints(breakpoint.type).find {
                    it is XLineBreakpoint<*> && it.fileUrl == targetFileXPosition.file.url && it.line == targetFileXPosition.line
                }

                if (!isRemoved) {
                    // add
                    if (existingTargetBreakPoint == null) {
                        breakpointManager.addLineBreakpoint(
                            breakpoint.type,
                            targetFileXPosition.file.url,
                            targetFileXPosition.line,
                            null,
                        )
                    }
                } else {
                    // remove
                    if (existingTargetBreakPoint != null) {
                        breakpointManager.removeBreakpoint(existingTargetBreakPoint)
                    }
                }
            }
        }
    }
}
