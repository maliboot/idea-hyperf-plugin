package io.maliboot.devkit.idea.hyperf.composer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.composer.ComposerConfigUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ComposerUtils {
    private final VirtualFile myConfigFile;
    private final Project myProject;
    private JsonObject myRoot = null;
    private final Logger LOG = Logger.getInstance(ComposerUtils.class);

    public ComposerUtils(@NotNull Project project) {
        this(project, Objects.requireNonNull(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(project.getBasePath() + "/composer.json"))));
    }

    public ComposerUtils(@NotNull Project project, @NotNull VirtualFile configFile) {
        this.myConfigFile = configFile;
        this.myProject = project;

        try {
            this.myRoot = ComposerConfigUtils.parseJson(myConfigFile).getAsJsonObject();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public VirtualFile getConfigFile() {
        return myConfigFile;
    }

    public @Nullable String getConfig(String key) {
        if (this.myRoot == null) {
            return null;
        }
        JsonObject root = myRoot.deepCopy();
        // 支持点语法
        String[] keys = key.split("\\.");

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].isEmpty()) {
                continue;
            }

            // 最后一位为值
            if (root.isJsonPrimitive()) {
                if (keys.length - 1 != i) {
                    return null;
                }
                return root.getAsJsonPrimitive(keys[i]).getAsString();
            }
            root = root.getAsJsonObject(keys[i]);
        }
        if (root == null) {
            return null;
        }
        return root.toString();
    }

    /**
     * 批量设置配置信息
     * 针对键值对为string-string对进行批量更新，如{"name":"hyperf-skeleton", "type":"project"}
     *
     * @param myEntries 一个包含配置项的映射表，不能为空
     */
    public void setConfig(@NotNull Map<String, String> myEntries, boolean isSave) {
        if (this.myRoot == null) {
            LOG.error("No root node for composer.json");
            return;
        }

        if (! myEntries.isEmpty()) {
            for (Map.Entry<String, String> entry : myEntries.entrySet()) {
                JsonElement prop = this.myRoot.get(entry.getKey());
                if (prop == null) {
                    return;
                }
                this.myRoot.addProperty(entry.getKey(), entry.getValue());
            }
        }

        if (isSave) {
            setText(null);
        }
    }

    /**
     * 批量设置配置信息
     * 针对键值对为string-array对进行批量更新，如{"require":{"php": "8.0"}}
     *
     * @param values 一个包含配置项的映射表，不能为空
     */
    public void setConfig(String key, ArrayList<Map<String, String>> values, boolean isSave) {
        if (this.myRoot == null) {
            LOG.error("No root node for composer.json");
            return;
        }

        if (! values.isEmpty()) {
            JsonElement prop = this.myRoot.get(key);
            if (prop == null || !prop.isJsonObject()) {
                return;
            }
            for (Map<String, String>value: values) {
                for (Map.Entry<String, String> entry: value.entrySet()) {
                    prop.getAsJsonObject().addProperty(entry.getKey(), entry.getValue());
                }
            }
        }

        if (isSave) {
            setText(null);
        }
    }

    /**
     * 批量设置配置信息
     * 针对键值对为string-array对进行批量更新，如: {repositories: [{"type": "composer","url": "repo.packagist.org"}]}
     *
     * @param values 一个包含配置项的映射表，不能为空
     */
    public void setConfigByArrayJson(String key, ArrayList<ArrayList<Map<String, String>>> values, boolean isSave) {
        if (this.myRoot == null) {
            LOG.error("No root node for composer.json");
            return;
        }

        if (! values.isEmpty()) {
            JsonElement prop = this.myRoot.get(key);
            if (prop == null) {
                prop = new JsonArray();
                this.myRoot.add(key, prop);
            }
            if (!prop.isJsonArray()) {
                return;
            }
            for (ArrayList<Map<String, String>>valueArr: values) {
                for (Map<String, String>valueMap: valueArr) {
                    JsonObject node  = new JsonObject();
                    valueMap.forEach(node::addProperty);

                    // 排重
                    if (prop.getAsJsonArray().contains(node)) {
                        continue;
                    }
                    prop.getAsJsonArray().add(node);
                }
            }
        }

        if (isSave) {
            setText(null);
        }
    }

    public void setText(@Nullable Runnable runnable) {
        WriteCommandAction.runWriteCommandAction(myProject, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(myConfigFile);
            if (document == null) {
                throw new IllegalStateException("No document found for file: " + myConfigFile.getPath());
            }
            // save
            document.setText(this.myRoot.toString());
            FileDocumentManager.getInstance().saveDocument(document);
            // format
            PsiFile psiFile = PsiManager.getInstance(myProject).findFile(myConfigFile);
            if (psiFile != null) {
                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myProject);
                codeStyleManager.reformat(psiFile);
            }
            // refresh
            myConfigFile.refresh(false, true);
            if (runnable != null) {
                runnable.run();
            }
        });
    }
}

