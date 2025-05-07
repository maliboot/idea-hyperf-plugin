package io.maliboot.devkit.idea.hyperf.debug

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpointListener

class HyperfStartupActivity : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        // 订阅调试会话事件
        project.messageBus.connect().subscribe(XDebuggerManager.TOPIC, HyperfXDebuggerManagerListener())
        // 断点动态映射
        project.messageBus.connect().subscribe(XBreakpointListener.TOPIC, HyperfBreakpointListener(project))
    }
}
