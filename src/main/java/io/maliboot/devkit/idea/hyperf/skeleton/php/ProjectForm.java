package io.maliboot.devkit.idea.hyperf.skeleton.php;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.InplaceButton;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.components.ActionLink;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProjectForm {
    private JButton serverUrlLink;
    private JPanel serverUrlJPanel;
    private JLabel serverUrlLabel;
    private JLabel locationLabel;
    private JLabel projectNameLabel;
    private JLabel projectDescLabel;
    private JTextField projectDescJText;
    private JTextField vendorNameJText;
    private JTextField projectNameJText;
    private JLabel vendorNameLabel;
    private JLabel phpVersionLabel;
    private JPanel phpVersionJPanel;
    private SegmentedBtnComp phpVersionSegComp;
    private JLabel versionLabel;
    private JPanel versionJPanel;
    private SegmentedBtnComp versionSegComp;
    private JLabel keywordsLabel;
    private JPanel keywordsJPanel;
    private SegmentedBtnComp keywordsSegComp;
    private JLabel packagistUrlLabel;
    private JComboBox<PackagistUrl> packagistUrlJCombo;
    private JPanel mainJPanel;
    private JPanel locationJPanel;
    private LinkedHashMap<JLabel, JComponent> labeledComponents;

    static String LOCATION_FLAG = "location";

    public ProjectForm() {
        $$$setupUI$$$();
        init();
        initLabeledComponents();
    }

    public LinkedHashMap<JLabel, JComponent> getLabeledComponents() {
        return this.labeledComponents;
    }

    private void init() {
        this.serverUrlLink.setText("hyperf.io");
        this.serverUrlLink.setName("https://github.com/hyperf/hyperf-skeleton/archive/refs/tags");

        // 设置服务器地址
        InplaceButton serverUrlSettingBtn = new InplaceButton(
                "设置服务器地址",
                AllIcons.General.Settings,
                e -> (new ServerUrlDialogWrapper()).show()
        );
        serverUrlSettingBtn.setPreferredSize(new JBDimension(25, 25));
        this.serverUrlJPanel.add(serverUrlSettingBtn);

        // 检查服务器
        InplaceButton serverUrlVerifyBtn = new InplaceButton(
                "服务器地址验证",
                AllIcons.General.Error,
                e -> (new ServerUrlDialogWrapper()).show()
        );
        serverUrlVerifyBtn.setVisible(false);
        serverUrlVerifyBtn.setPreferredSize(new JBDimension(25, 25));
        this.serverUrlJPanel.add(serverUrlVerifyBtn);

        // 项目location,根项目位置为IDE控制，所以此处不设置
        // 由ProjectSpecificSettingsStep.createLocationComponent创建,并且在重写调整空间位置
        this.locationLabel.setText(LOCATION_FLAG);

        // 镜像源
        ArrayList<PackagistUrl> packagistUrls = new ArrayList<>();
        packagistUrls.add(new PackagistUrl("阿里云", "https://mirrors.aliyun.com/composer/"));
        packagistUrls.add(new PackagistUrl("腾讯云", "https://mirrors.cloud.tencent.com/composer/"));
        packagistUrls.add(new PackagistUrl("中国全量镜像", "https://packagist.phpcomposer.com"));
        this.packagistUrlJCombo.setModel(new CollectionComboBoxModel<>(packagistUrls));
        this.packagistUrlJCombo.setEditable(true);
    }

    private void initLabeledComponents() {
        this.labeledComponents = new LinkedHashMap<>();
        // 服务器url
        this.labeledComponents.put(serverUrlLabel, serverUrlJPanel);
        // 项目名称
        this.labeledComponents.put(projectNameLabel, projectNameJText);
        // 组织名称
        this.labeledComponents.put(vendorNameLabel, vendorNameJText);
        // 项目描述
        this.labeledComponents.put(projectDescLabel, projectDescJText);
        // 项目位置
        this.labeledComponents.put(locationLabel, locationJPanel);
        // php版本
        this.labeledComponents.put(phpVersionLabel, phpVersionJPanel);
        // 框架版本
        this.labeledComponents.put(versionLabel, versionJPanel);
        // 关键词
        this.labeledComponents.put(keywordsLabel, keywordsJPanel);
        // 镜像源
        this.labeledComponents.put(packagistUrlLabel, packagistUrlJCombo);
    }

    private void createUIComponents() {
        this.serverUrlLink = new ActionLink("", event -> {
            BrowserUtil.browse(this.serverUrlLink.getName());
        });

        // php版本
        ArrayList<String> phpVersionList = new ArrayList<>();
        phpVersionList.add("8.0");
        phpVersionList.add("8.1");
        phpVersionList.add("8.2");
        phpVersionList.add("8.3");
        this.phpVersionSegComp = (new SegmentedBtnComp()).create(phpVersionList);
        this.phpVersionJPanel = this.phpVersionSegComp.getDialogPanel();

        // 框架版本
        ArrayList<String> frameworkVersionList = new ArrayList<>();
        frameworkVersionList.add("v3.0.2");
        frameworkVersionList.add("v3.1.2");
        this.versionSegComp = (new SegmentedBtnComp()).create(frameworkVersionList);
        this.versionJPanel = this.versionSegComp.getDialogPanel();


        // 项目关键词
        ArrayList<String> keywordList = new ArrayList<>();
        keywordList.add("skeleton");
        keywordList.add("swoole");
        keywordList.add("hyperf");
        this.keywordsSegComp = (new SegmentedBtnComp()).create(keywordList);
        this.keywordsJPanel = this.keywordsSegComp.getDialogPanel();

        // 镜像源
        this.packagistUrlJCombo = new ComboBox<>();
        this.packagistUrlJCombo.setRenderer(new SimpleListCellRenderer<>() {
            @Override
            public void customize(@NotNull JList<? extends PackagistUrl> list, PackagistUrl value, int index, boolean selected, boolean hasFocus) {
                setText(value.name());
            }

            @Override
            public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        });
    }

    public JButton getServerUrlLink() {
        return serverUrlLink;
    }

    public JTextField getProjectDescJText() {
        return projectDescJText;
    }

    public JTextField getVendorNameJText() {
        return vendorNameJText;
    }

    public JTextField getProjectNameJText() {
        return projectNameJText;
    }

    public JComboBox<PackagistUrl> getPackagistUrlJCombo() {
        return packagistUrlJCombo;
    }

    public SegmentedBtnComp getVersionSegComp() {
        return versionSegComp;
    }

    public SegmentedBtnComp getPhpVersionSegComp() {
        return phpVersionSegComp;
    }

    public SegmentedBtnComp getKeywordsSegComp() {
        return keywordsSegComp;
    }

    public JPanel getMainJPanel() {
        return mainJPanel;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainJPanel = new JPanel();
        mainJPanel.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        serverUrlLabel = new JLabel();
        serverUrlLabel.setText("服务器地址");
        mainJPanel.add(serverUrlLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainJPanel.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        locationLabel = new JLabel();
        locationLabel.setText("Location");
        mainJPanel.add(locationLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectNameLabel = new JLabel();
        projectNameLabel.setText("名称");
        mainJPanel.add(projectNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectDescLabel = new JLabel();
        projectDescLabel.setText("描述");
        mainJPanel.add(projectDescLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectDescJText = new JTextField();
        projectDescJText.setText("框架脚手架");
        mainJPanel.add(projectDescJText, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        serverUrlJPanel = new JPanel();
        serverUrlJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mainJPanel.add(serverUrlJPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        serverUrlLink.setText("");
        serverUrlJPanel.add(serverUrlLink);
        vendorNameJText = new JTextField();
        vendorNameJText.setText("Hyperf");
        mainJPanel.add(vendorNameJText, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vendorNameLabel = new JLabel();
        vendorNameLabel.setText("组织");
        mainJPanel.add(vendorNameLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectNameJText = new JTextField();
        projectNameJText.setText("hyperf-skeleton");
        mainJPanel.add(projectNameJText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        phpVersionLabel = new JLabel();
        phpVersionLabel.setText("PHP版本");
        mainJPanel.add(phpVersionLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainJPanel.add(phpVersionJPanel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        versionLabel = new JLabel();
        versionLabel.setText("框架版本");
        mainJPanel.add(versionLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainJPanel.add(versionJPanel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        keywordsLabel = new JLabel();
        keywordsLabel.setText("关键词");
        mainJPanel.add(keywordsLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainJPanel.add(keywordsJPanel, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        packagistUrlLabel = new JLabel();
        packagistUrlLabel.setText("镜像源");
        mainJPanel.add(packagistUrlLabel, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainJPanel.add(packagistUrlJCombo, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainJPanel;
    }

    public class ServerUrlDialogWrapper extends DialogWrapper {

        private JTextField serverUrlSetting;

        public ServerUrlDialogWrapper() {
            super(true);
            setTitle("设置服务器地址");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel serverUrlSettingDialog = new JPanel(new BorderLayout());
            serverUrlSettingDialog.setPreferredSize(new JBDimension(266, -1));
            this.serverUrlSetting = new JTextField(serverUrlLink.getName());
            serverUrlSettingDialog.add(serverUrlSetting, BorderLayout.CENTER);
            return serverUrlSettingDialog;
        }

        @Override
        protected void applyFields() {
            String urlText = serverUrlSetting.getText();
            serverUrlLink.setName(urlText);
            serverUrlLink.setText(urlText.replaceFirst("https*://", ""));
        }
    }

    public record PackagistUrl(String name, String url) {
        @Override
            public @NotNull String toString() {
                return url;
            }
        }
}
