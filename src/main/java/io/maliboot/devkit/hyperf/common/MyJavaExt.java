package io.maliboot.devkit.hyperf.common;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;

public class MyJavaExt {

    // fix.兼容某些SDK版本依赖异常报错-> java.lang.NoSuchFieldError: Companion
    public static Boolean isDump(Project project) {
        return DumbService.getInstance(project).isDumb();
    }
}
