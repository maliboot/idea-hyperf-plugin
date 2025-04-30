package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface HyperfIcons {
    Icon Plugin = IconLoader.getIcon("/META-INF/pluginIcon.svg", HyperfIcons.class);
    Icon HyperfIcon = IconLoader.getIcon("/icons/hyperf.ico", HyperfIcons.class);
    Icon Hyperf = IconLoader.getIcon("/icons/hyperf.svg", HyperfIcons.class);
    Icon Composer = IconLoader.getIcon("/icons/composer.svg", HyperfIcons.class);
}
