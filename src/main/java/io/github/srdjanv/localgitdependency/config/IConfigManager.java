package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IConfigManager extends Manager {
    static IConfigManager createInstance(Managers managers) {
        return new ConfigManager(managers);
    }
    PluginConfig getPluginConfig();
    DefaultableConfig getDefaultableConfig();
    @TaskDescription("finalizing configs")
    void finalizeConfigs();
}
