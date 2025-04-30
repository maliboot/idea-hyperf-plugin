package io.maliboot.devkit.idea.hyperf.skeleton.php;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.observable.properties.PropertyGraph;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.components.JBCheckBox;
import com.jetbrains.php.ComposerDependency;
import com.jetbrains.php.ComposerPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProjectPeer implements ProjectGeneratorPeer<ProjectPeer.MyProjectSettings> {

    private final ComposerPanel myComposerPanel = new ComposerPanel(new PropertyGraph());

    private final ProjectForm myProjectForm = new ProjectForm();

    public ProjectPeer() {
        JComponent panel = this.myComposerPanel.getPanel();
        if (panel.getComponents()[0] instanceof JBCheckBox composerHelpComp) {
            composerHelpComp.setText("依赖包添加");
        }
    }

    @Override
    public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid) {
        // location field
        return this.myComposerPanel.getPanel();
    }

    @Override
    public void buildUI(@NotNull SettingsStep settingsStep) {
        for (Map.Entry<JLabel, JComponent> map : this.myProjectForm.getLabeledComponents().entrySet()) {
            settingsStep.addSettingsField(map.getKey().getText(), map.getValue());
        }
        settingsStep.addSettingsComponent(this.myComposerPanel.getPanel());
    }

    @Override
    public @NotNull MyProjectSettings getSettings() {
        return new MyProjectSettings(
                this.myProjectForm.getServerUrlLink().getName(),
                this.myProjectForm.getVersionSegComp().getSelectedText(),
                this.myProjectForm.getPhpVersionSegComp().getSelectedText(),
                this.myProjectForm.getVendorNameJText().getText(),
                this.myProjectForm.getProjectNameJText().getText(),
                this.myProjectForm.getProjectDescJText().getText(),
                null,
                Objects.requireNonNull(this.myProjectForm.getPackagistUrlJCombo().getSelectedItem()).toString(),
                this.myComposerPanel.getRequireDependencies(),
                this.myComposerPanel.getRequireDevDependencies()
        );
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return null;
    }

    @Override
    public boolean isBackgroundJobRunning() {
        return false;
    }

    public static class MyProjectSettings {
        private final String myServerUrl;
        private final String myVersion;
        private final String myPhpVersion;
        private final String myVendorName;
        private final String myPkgName;
        private final String myDesc;
        private final List<String> myKeywords;
        private final String myPackagistUrl;
        private final List<ComposerDependency> myRequireDependencies;
        private final List<ComposerDependency> myRequireDevDependencies;

        public MyProjectSettings(
                String serverUrl,
                String version,
                String phpVersion,
                String vendorName,
                String pkgName,
                String desc,
                List<String> keywords,
                String packagistUrl,
                List<ComposerDependency> requireDependencies,
                List<ComposerDependency> requireDevDependencies
        ) {
            this.myServerUrl = serverUrl;
            this.myVersion = version;
            this.myPhpVersion = phpVersion;
            this.myVendorName = vendorName;
            this.myPkgName = pkgName;
            this.myDesc = desc;
            this.myKeywords = keywords;
            this.myPackagistUrl = packagistUrl;
            this.myRequireDependencies = requireDependencies;
            this.myRequireDevDependencies = requireDevDependencies;
        }

        public String getVersion() {
            return this.myVersion;
        }

        public String getPhpVersion() {
            return this.myPhpVersion;
        }

        public String getVendorName() {
            return this.myVendorName;
        }

        public String getPkgName() {
            return this.myPkgName;
        }

        public List<String> getKeywords() {
            return this.myKeywords;
        }

        public String getPackagistUrl() {
            return this.myPackagistUrl;
        }

        public String getServerUrl() {
            return myServerUrl;
        }

        public String getDesc() {
            return myDesc;
        }

        public List<ComposerDependency> getRequireDependencies() {
            return this.myRequireDependencies;
        }

        public List<ComposerDependency> getRequireDevDependencies() {
            return this.myRequireDevDependencies;
        }
    }
}
