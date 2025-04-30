package io.maliboot.devkit.idea.hyperf.composer;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableNotNullFunction;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.composer.ComposerPackagedVersionedCommandExecutor;
import com.jetbrains.php.composer.actions.ComposerCommandExecutor;
import com.jetbrains.php.composer.actions.log.ComposerLogMessageBuilder;
import com.jetbrains.php.composer.execution.executable.ExecutableComposerExecution;
import com.jetbrains.php.composer.statistics.ComposerActionStatistics;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ComposerCommandRequire extends ComposerPackagedVersionedCommandExecutor {
    protected ComposerCommandRequire(@NotNull Project project, @NotNull String packageName, String version, @Nullable String commandLineOptions) {
        super(
                project,
                packageName,
                version,
                project.getBasePath(),
                commandLineOptions,
                new ExecutableComposerExecution("composer"),
                ComposerActionStatistics.create(ComposerActionStatistics.Action.REQUIRE, ""),
                false
        );
    }

    @Override
    protected ComposerLogMessageBuilder.SummaryMessage createSuccessfulSummary() {
        return new ComposerLogMessageBuilder.SummaryMessage("Composer: " + this.myPackageName + " Successful");
    }

    @Override
    protected ComposerLogMessageBuilder.SummaryMessage createFailureSummary() {
        return new ComposerLogMessageBuilder.SummaryMessage("Composer: Failure");
    }

    @Override
    protected boolean handleAfterCommandExecution(boolean success) {
        ComposerUtils myComposer = new ComposerUtils(myProject);
        VirtualFile myComposerConfig = myComposer.getConfigFile();
        if (myComposerConfig != null) {
            myComposerConfig.refresh(true, false);
        }
        return super.handleAfterCommandExecution(success);
    }

    @Override
    protected @Nls String getProgressTitle() {
        return "Composer: " + this.myPackageName + " ...";
    }

    @Override
    protected String getActionName() {
        return "Composer: " + this.myPackageName + " ...";
    }

    @Override
    protected @NotNull List<String> getBasicCommand() {
        return com.jetbrains.php.composer.ComposerUtils.getInstallationCommand(this.myPackageName, this.myVersion);
    }

    @Override
    protected @Nullable ThrowableNotNullFunction<Project, ComposerCommandExecutor, ExecutionException> getExecutorGenerator() {
        return null;
    }
}
