package cn.layne666.common;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(name = "PatcherConfig", storages = {@Storage("PatcherConfig.xml")})
public class PatcherConfig implements PersistentStateComponent<PatcherConfig> {
    private Map<String, String> modulePathMap = new HashMap<>();
    private Map<String, String> exportPathMap = new HashMap<>();

    @Nullable
    @Override
    public PatcherConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PatcherConfig patcherConfig) {
        XmlSerializerUtil.copyBean(patcherConfig, this);
    }

    @Nullable
    public static PatcherConfig getInstance(Project project) {
        return ServiceManager.getService(project, PatcherConfig.class);
    }

    public Map<String, String> getModulePathMap() {
        return modulePathMap;
    }

    public void setModulePathMap(Map<String, String> modulePathMap) {
        this.modulePathMap = modulePathMap;
    }

    public Map<String, String> getExportPathMap() {
        return exportPathMap;
    }

    public void setExportPathMap(Map<String, String> exportPathMap) {
        this.exportPathMap = exportPathMap;
    }
}