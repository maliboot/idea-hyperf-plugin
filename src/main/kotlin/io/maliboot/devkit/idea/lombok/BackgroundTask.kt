package io.maliboot.devkit.idea.lombok

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.platform.templates.github.DownloadUtil
import java.io.File

/**
 * 在 IntelliJ/PhpStorm 插件中，使用 Task.Backgroundable 异步下载文件并在 UI 上显示进度
 */
object BackgroundTask {
    fun run(
        project: Project ,
        title: String,
        onEvent: ((indicator: ProgressIndicator) -> Unit),
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        canBeCancel: Boolean = false
    ) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, title, canBeCancel) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    onEvent.invoke(indicator)
                    // 下载完成后切回 UI 线程通知用户
                    ApplicationManager.getApplication().invokeLater {
                        onSuccess?.invoke() ?: showInfoNotification(
                            project,
                            "Task[${title}] success",
                            "$title-Success"
                        )
                    }
                } catch (e: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        onError?.invoke(e) ?: showErrorNotification(
                            project,
                            "$title-Error",
                            "$title-Error：${e.message}"
                        )
                    }
                }
            }
        })
    }

    /**
     * 启动异步下载任务
     * @param project 当前项目上下文
     * @param isCover 是否覆盖
     * @param fileUrl  下载链接
     * @param output    本地保存路径
     */
    fun download(project: Project, isCover: Boolean, title: String, fileUrl: String, output: File, onSuccess: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        run(project, title, {
            if (!isCover && output.exists()) {
                return@run
            }
            DownloadUtil.downloadAtomically(it, fileUrl, output)
        }, onSuccess, onError, false)
    }

    fun showErrorNotification(project: Project?, title: String, content: String) {
        Notifications.Bus.notify(
            Notification(
                "HyperfNotificationGroup",
                title,
                content,
                NotificationType.ERROR
            ), project
        )
    }

    fun showInfoNotification(project: Project?, title: String, content: String) {
        Notifications.Bus.notify(
            Notification(
                "HyperfNotificationGroup",
                title,
                content,
                NotificationType.INFORMATION
            ), project
        )
    }
}
