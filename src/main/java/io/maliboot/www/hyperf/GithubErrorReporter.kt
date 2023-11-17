package io.maliboot.www.hyperf

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.ArrayUtil
import com.intellij.util.Consumer
import java.awt.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Generates an issue creation link
 * https://github.com/maliboot/idea-hyperf-plugin/issues/new?title=foo&body=bar
 */
class GithubErrorReporter : ErrorReportSubmitter() {
    companion object {
        private const val URL = "https://github.com/maliboot/idea-hyperf-plugin/issues/new"
        private const val PLUGIN_ID = "io.maliboot.www.hyperf"
    }

    override fun getReportActionText(): String {
        return "Report to Maliboot"
    }

    @Suppress("SwallowedException")
    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val event = ArrayUtil.getFirstElement(events)
        var title = "[IDEA-Plugin]Exception: "
        var stacktrace = "Please paste the full stacktrace from the IDEA error popup.\n"
        if (event != null) {
            val throwableText = event.throwableText
            val exceptionTitle: String = throwableText.lines().first()
            title += if (!StringUtil.isEmptyOrSpaces(exceptionTitle)) exceptionTitle else "<Fill in title>"
            if (!StringUtil.isEmptyOrSpaces(throwableText)) {
                val quotes = "\n```\n"
                stacktrace += quotes + StringUtil.first(
                    throwableText,
                    6000,
                    true
                ) + quotes
            }
        }
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
        val pluginVersion = if (plugin != null) plugin.version else ""
        val ideaVersion = ApplicationInfo.getInstance().build.asString()
        val template = StringBuilder()
        template.append("### Description\n")
        if (additionalInfo != null) {
            template.append(additionalInfo).append("\n")
        }
        template.append("\n")
        template.append("### Stacktrace\n").append(stacktrace).append("\n")
        template.append("### Version and Environment Details\n")
            .append("Operation system: ").append(SystemInfo.getOsNameAndVersion()).append("\n")
            .append("IDE version: ").append(ideaVersion).append("\n")
            .append("Plugin version: ").append(pluginVersion).append("\n")
        val charset = StandardCharsets.UTF_8
        val url = String.format(
            "%s?title=%s&labels=%s&body=%s",
            URL,
            URLEncoder.encode(title, charset),
            URLEncoder.encode("error", charset),
            URLEncoder.encode(template.toString(), charset)
        )
        BrowserUtil.browse(url)
        consumer.consume(
            SubmittedReportInfo(
                null,
                "GitHub issue",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
            )
        )
        return true
    }
}