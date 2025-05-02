package io.maliboot.devkit.idea.hyperf.skeleton.php;

import com.intellij.ide.IdeCoreBundle;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase;
import com.intellij.ide.util.projectWizard.WebProjectSettingsStepWrapper;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.lang.javascript.boilerplate.AbstractGithubTagDownloadedProjectGenerator;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.templates.TemplateProjectDirectoryGenerator;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProjectSpecificSettingsStep extends ProjectSettingsStepBase {

    public ProjectSpecificSettingsStep(DirectoryProjectGenerator projectGenerator, AbstractNewProjectStep.AbstractCallback callback) {
        super(projectGenerator, callback);
    }

    public JPanel createPanel() {
        JPanel var1 = super.createPanel();
        JPanel var2 = new JPanel(new BorderLayout());
        JLabel var3 = new JLabel(IdeCoreBundle.message("title.new.project", new Object[0]));
        var3.setFont(var3.getFont().deriveFont(1));
        var2.add(var3, "West");
        if (this.myProjectGenerator instanceof AbstractGithubTagDownloadedProjectGenerator) {
            ActionLink var4 = ((AbstractGithubTagDownloadedProjectGenerator)this.myProjectGenerator).createGitHubLink();
            var2.add(var4, "East");
        }

        var2.setBorder(JBUI.Borders.emptyBottom(10));
        var1.add(var2, "North");
        return var1;
    }

    @Override
    protected JPanel createAndFillContentPanel() {
        WebProjectSettingsStepWrapper settingsStep = new WebProjectSettingsStepWrapper(this);
        if (myProjectGenerator instanceof WebProjectTemplate) {
            getPeer().buildUI(settingsStep);
        } else if (myProjectGenerator instanceof TemplateProjectDirectoryGenerator) {
            ((TemplateProjectDirectoryGenerator<?>) myProjectGenerator).buildUI(settingsStep);
        } else {
            return createContentPanelWithAdvancedSettingsPanel();
        }

        //back compatibility: some plugins can implement only GeneratorPeer#getComponent() method
        if (settingsStep.isEmpty()) return createContentPanelWithAdvancedSettingsPanel();

        final JPanel jPanel = new JPanel(new VerticalFlowLayout(13, 10));
        List<LabeledComponent<? extends JComponent>> labeledComponentList = new ArrayList<>(settingsStep.getFields());

        final JPanel scrollPanel = new JPanel(new BorderLayout());
        scrollPanel.add(jPanel, BorderLayout.NORTH);

        LabeledComponent localComp = createLocationComponent();
        localComp.getLabel().setText("项目位置");
        for (LabeledComponent<? extends JComponent> component : labeledComponentList) {
            component.setLabelLocation(BorderLayout.WEST);
            component.getLabel().setPreferredSize(new JBDimension(85, -1));
            if (component.getLabel().getText().equals(ProjectForm.LOCATION_FLAG + ":")) {
                localComp.setLabelLocation(BorderLayout.WEST);
                localComp.getLabel().setPreferredSize(new JBDimension(85, -1));
                jPanel.add(localComp);
                continue;
            }
            jPanel.add(component);
        }

        for (JComponent component : settingsStep.getComponents()) {
            jPanel.add(component);
        }

        UIUtil.mergeComponentsWithAnchor(labeledComponentList);

        return scrollPanel;
    }

    @Override
    protected @NotNull Path findSequentNonExistingUntitled() {
        return FileUtil.findSequentNonexistentFile(new File(ProjectUtil.getBaseDir()), "hyperf-skeleton", "").toPath();
    }
}
