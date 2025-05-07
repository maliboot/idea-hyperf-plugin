package io.maliboot.devkit.idea.hyperf.skeleton.php;

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.platform.templates.github.GeneratorException;
import com.intellij.platform.templates.github.ZipUtil;
import com.intellij.util.io.URLUtil;
import com.jetbrains.php.ComposerDependency;
import icons.HyperfIcons;
import io.maliboot.devkit.idea.hyperf.composer.ComposerCommandInstall;
import io.maliboot.devkit.idea.hyperf.composer.ComposerUtils;
import io.maliboot.devkit.idea.lombok.BackgroundTask;
import kotlin.Unit;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ProjectGenerator extends WebProjectTemplate<ProjectPeer.MyProjectSettings> implements CustomStepProjectGenerator {

    private final Logger LOG = Logger.getInstance(ProjectGenerator.class);

    public ProjectGenerator() {
    }

    public static File findCacheFile(@NotNull String cacheFileName) {
        File dir = new File(new File(PathManager.getSystemPath(), "projectGenerators"), "hyperf-start");
        try {
            dir = dir.getCanonicalFile();
        } catch (IOException ignored) {
        }
        return new File(dir, cacheFileName);
    }

    @Override
    public String getDescription() {
        return "Hyperf scaffold";
    }

    @Override
    public @NotNull String getName() {
        return '\1' + "Hyperf Project";
    }

    @Override
    public Icon getIcon() {
        return HyperfIcons.Composer;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, ProjectPeer.@NotNull MyProjectSettings settings, @NotNull Module module) {
        String url = Strings.trimEnd(settings.getServerUrl(), "/");
        boolean isGitRepo = url.startsWith("https://git") || url.startsWith("https://mirrors");
        if (!url.endsWith(".zip")) {
            if (isGitRepo) {
                url = url + "/" + settings.getVersion() + ".zip";
            } else {
                url = url + getQryParams(settings);
            }
        }

        String fileName = URLUtil.encodeURIComponent(DigestUtils.md5Hex(url) + ".zip");
        @NotNull File zipArchiveFile = findCacheFile(fileName);

        String taskTitle = "项目创建";

        // 下载骨架
        BackgroundTask.INSTANCE.download(project, false, taskTitle + "骨架Zip下载", url, zipArchiveFile, () -> {
            // 解压缩包
            String unzipTitle = taskTitle + "骨架Zip解压";
            try {
                ZipUtil.unzipWithProgressSynchronously(project, unzipTitle, zipArchiveFile, VfsUtilCore.virtualToIoFile(baseDir), null, true);
                // json文件改写
                if (isGitRepo) {
                    BackgroundTask.INSTANCE.run(project, "整理配置", (indicator) -> {
                        ComposerUtils composerUtils = new ComposerUtils(project);
                        composerUtils.setConfig(Map.of(
                                "name", settings.getPkgName() + "/" + settings.getVendorName(),
                                "description", settings.getDesc()
                        ), false);
                        // require
                        ArrayList<Map<String, String>> requireData = new ArrayList<>();
                        requireData.add(Map.of("php", ">=" + settings.getPhpVersion()));
                        settings.getRequireDependencies().forEach(dependency -> {
                            String myVer = dependency.getVersion();
                            if (myVer.contains("default")) {
                                myVer = "*";
                            }
                            requireData.add(Map.of(dependency.getName(), myVer));
                        });
                        composerUtils.setConfig("require", requireData, false);
                        // require-dev
                        ArrayList<Map<String, String>> requireDevData = new ArrayList<>();
                        settings.getRequireDevDependencies().forEach(dependency -> {
                            String myVer = dependency.getVersion();
                            if (myVer.contains("default")) {
                                myVer = "*";
                            }
                            requireDevData.add(Map.of(dependency.getName(), myVer));
                        });
                        composerUtils.setConfig("require-dev", requireDevData, false);
                        // repositories
                        ArrayList<Map<String, String>> repositoriesData = new ArrayList<>();
                        repositoriesData.add(Map.of(
                                "type", "composer",
                                "url", settings.getPackagistUrl()
                        ));
                        ArrayList<ArrayList<Map<String, String>>> repositoriesOuterData = new ArrayList<>();
                        repositoriesOuterData.add(repositoriesData);
                        composerUtils.setConfigByArrayJson("repositories", repositoriesOuterData, true);
                        return Unit.INSTANCE;
                    },
                    () -> {
                        new ComposerCommandInstall(project, taskTitle, "--no-interaction --prefer-dist").execute();
                        return Unit.INSTANCE;
                    }, null, false);
                }
            } catch (GeneratorException e) {
                BackgroundTask.INSTANCE.showErrorNotification(project, unzipTitle, String.format("框架脚手架安装错误:%s，解压失败:%s", e.getMessage(), zipArchiveFile.getAbsolutePath()));
            }

            return Unit.INSTANCE;
        }, null);
    }

    private @NotNull String getQryParams(ProjectPeer.@NotNull MyProjectSettings settings) {
        StringBuilder requireDev = new StringBuilder();
        for (int i = 0; i < settings.getRequireDevDependencies().size(); i++) {
            ComposerDependency requireDevDependency = settings.getRequireDevDependencies().get(i);
            requireDev.append(requireDevDependency.getName()).append(":").append(requireDevDependency.getVersion()).append(",");
        }
        StringBuilder require = new StringBuilder();
        for (int i = 0; i < settings.getRequireDependencies().size(); i++) {
            ComposerDependency requireDependency = settings.getRequireDependencies().get(i);
            require.append(requireDependency.getName()).append(":").append(requireDependency.getVersion()).append(",");
        }
        return String.format(
                "?vendorName=%s&packageName=%s&phpVersion=%s&version=%s&desc=%s&packagistUrl=%s&requireDev=%s&require=%s",
                settings.getVendorName(),
                settings.getPkgName(),
                settings.getPhpVersion(),
                settings.getVersion(),
                URLUtil.encodeURIComponent(settings.getDesc()),
                URLUtil.encodeURIComponent(settings.getPackagistUrl()),
                URLUtil.encodeURIComponent(requireDev.toString()),
                URLUtil.encodeURIComponent(require.toString())
        );
    }

    @Override
    public @NotNull ProjectGeneratorPeer<ProjectPeer.MyProjectSettings> createPeer() {
        return new ProjectPeer();
    }

    @Override
    public AbstractActionWithPanel createStep(DirectoryProjectGenerator projectGenerator, AbstractNewProjectStep.AbstractCallback callback) {
        return new ProjectSpecificSettingsStep(projectGenerator, callback);
    }
}
