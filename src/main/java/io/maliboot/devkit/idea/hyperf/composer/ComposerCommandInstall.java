package io.maliboot.devkit.idea.hyperf.composer;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableNotNullFunction;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.actions.ComposerActionCommandExecutor;
import com.jetbrains.php.composer.actions.ComposerCommandExecutor;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.composer.actions.log.ComposerLogService;
import com.jetbrains.php.composer.execution.executable.ExecutableComposerExecution;
import com.jetbrains.php.composer.statistics.ComposerActionStatistics;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ComposerCommandInstall extends ComposerActionCommandExecutor {
    private final String taskTitle;

    public ComposerCommandInstall(@NotNull Project project, @NotNull String taskTitle, @Nullable String commandLineOptions) {
        super(
                project,
                project.getBasePath(),
                new ExecutableComposerExecution("composer"),
                commandLineOptions,
                ComposerActionStatistics.create(ComposerActionStatistics.Action.INSTALL, ""),
                true,
                true
        );
        this.taskTitle = taskTitle;
    }

    @Override
    protected ComposerLogMessageBuilder.SummaryMessage createSuccessfulSummary() {
        return new ComposerLogMessageBuilder.SummaryMessage("["+taskTitle+"]Composer Install:  Successful");
    }

    @Override
    protected ComposerLogMessageBuilder.SummaryMessage createFailureSummary() {
        return new ComposerLogMessageBuilder.SummaryMessage("["+taskTitle+"]请手动接管: 解决报错问题后\n运行命令: composer install");
    }

    @Override
    protected boolean handleAfterCommandExecution(boolean success) {
        ComposerUtils myComposer = new ComposerUtils(myProject);
        VirtualFile myComposerConfig = myComposer.getConfigFile();
        if (myComposerConfig != null) {
            com.jetbrains.php.composer.ComposerUtils.refreshVendorDir(myComposerConfig);
        }
        return super.handleAfterCommandExecution(success);
    }

    protected void handleBeforeCommandExecution() {
        ApplicationManager.getApplication().invokeLater(() -> ComposerLogService.getInstance(myProject).getConsoleView().show(null));
    }

    @Override
    protected @Nls String getProgressTitle() {
        return "["+taskTitle+"]Composer Install: 初始化加载中";
    }

    @Override
    protected String getActionName() {
        return "["+taskTitle+"]Composer Install";
    }

    @Override
    protected @NotNull List<String> getBasicCommand() {
        return Collections.singletonList("install");
    }

    @Override
    protected @Nullable ThrowableNotNullFunction<Project, ComposerCommandExecutor, ExecutionException> getExecutorGenerator() {
        return null;
    }

    @Override
    protected @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getTaskTitle() {
        return taskTitle;
    }
}
