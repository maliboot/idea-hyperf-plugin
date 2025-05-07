package io.maliboot.devkit.idea.hyperf.debug

import com.intellij.openapi.application.ReadAction
import com.intellij.xdebugger.*
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.jetbrains.php.debug.common.PhpDebugProcess
import com.jetbrains.php.debug.common.PhpExecutionStack
import com.jetbrains.php.debug.xdebug.connection.XdebugConnection
import com.jetbrains.php.debug.xdebug.debugger.XdebugStackFrame
import io.maliboot.devkit.idea.common.extend.isProxyFile
import io.maliboot.devkit.idea.hyperf.HyperfProxyFileUtil

class HyperfXDebuggerManagerListener: XDebuggerManagerListener {
    override fun processStarted(debugProcess: XDebugProcess) {
        if (debugProcess !is PhpDebugProcess<*>) {
            return
        }

        // 新调试会话启动时，添加会话监听器
        debugProcess.session.addSessionListener(
            MyDebugSessionListener(debugProcess.session)
        )
    }

    class MyDebugSessionListener(private val mySession: XDebugSession) : XDebugSessionListener {
        override fun sessionPaused() {
            val currentPosition = mySession.currentPosition ?: return
            val proxyFile = currentPosition.file
            if (! proxyFile.isProxyFile()) {
                return
            }

            val sourcePosition = ReadAction.compute<XSourcePosition?, RuntimeException> {
                return@compute HyperfProxyFileUtil.guessOriginXSourcePosition(currentPosition, mySession.project)
            } ?: return
            val currentFrame = mySession.currentStackFrame ?: return

            // 构造栈列表
            val executionStack = mySession.suspendContext?.activeExecutionStack ?: return
            val originalFrames = mutableListOf<XdebugStackFrame>()
            executionStack.computeStackFrames(0, object : XExecutionStack.XStackFrameContainer {
                override fun addStackFrames(frames: MutableList<out XStackFrame>, last: Boolean) {
                    frames.forEach {
                        if (it is XdebugStackFrame) {
                            originalFrames.add(it)
                        }
                    }
                }
                override fun errorOccurred(errorMessage: String) {
                    println("Stack frame error: $errorMessage")
                }
            })
            // 构造当前栈帧
            val sourceStackFrame = XdebugStackFrame(
                mySession.debugProcess as PhpDebugProcess<XdebugConnection?>,
                Runnable {},
                sourcePosition.file.path,
                sourcePosition.line,
                (currentFrame as XdebugStackFrame).functionName.substringBefore("->"),
                mySession.suspendContext?.executionStacks?.size ?: 1,
                0,
                null
            )

            // 栈顶添加模板文件栈帧
            originalFrames.add(0, sourceStackFrame)
            val newExecutionStack = PhpExecutionStack(originalFrames)
            // 会话重置执行栈列表、当前栈帧
            mySession.setCurrentStackFrame(newExecutionStack, originalFrames[0], true)
        }
    }
}