package io.github.srdjanv.localgitdependency.config;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IConfigManager extends Manager {
    static IConfigManager createInstance(Managers managers) {
        return new ConfigManager(managers);
    }

    void configurePlugin(@SuppressWarnings("rawtypes") Closure configureClosure);
    PluginConfig getPluginConfig();
    void configureDefaultable(@SuppressWarnings("rawtypes") Closure configureClosure);
    DefaultableConfig getDefaultableConfig();
    @TaskDescription("create essential directories")
    void createEssentialDirectories();
    @TaskDescription("configure configs")
    void configureConfigs();
}
