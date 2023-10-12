package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDefaultableConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultSourceSetMapper;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.config.plugin.impl.DefaultPluginConfig;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;

final class ConfigManager extends ManagerBase implements IConfigManager {
    private PluginConfig pluginConfig;
    private DefaultableConfig defaultableConfig;

    ConfigManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        // TODO: 28/09/2023 make classes not abstract to allow for older gradle compat
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

        var lgd = getLGDExtensionByType(LGDIDE.class);
        ClassUtil.finalizeProperties(lgd, LGDIDE.class);
        lgd.getMappers().forEach(mapper -> ((DefaultSourceSetMapper) mapper).finalizeProps());
    }
}
