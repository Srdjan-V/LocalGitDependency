package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDefaultableConfig;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.config.plugin.impl.DefaultPluginConfig;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;

final class ConfigManager extends ManagerBase implements IConfigManager {
    private PluginConfig pluginConfig;
    private DefaultableConfig defaultableConfig;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        pluginConfig = getProject().getObjects().newInstance(DefaultPluginConfig.class, this);
        defaultableConfig = getProject().getObjects().newInstance(DefaultDefaultableConfig.class, this);
    }

    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public DefaultableConfig getDefaultableConfig() {
        return defaultableConfig;
    }

    @Override
    public void finalizeConfigs() {
        ((DefaultPluginConfig) pluginConfig).finalizeProps();
        ((DefaultDefaultableConfig) defaultableConfig).finalizeProps();
    }
}
