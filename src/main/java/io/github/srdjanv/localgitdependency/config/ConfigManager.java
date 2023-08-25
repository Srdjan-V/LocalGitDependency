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
/*        var defaultPluginConfig = defaultPluginConfig();
        if (!pluginConfigBuilderConfigured) {
            pluginConfig = defaultPluginConfig;
        } else {
            pluginConfig = new PluginConfig(pluginConfigBuilder, getDefaultDir());
            pluginConfigBuilder = null;
            customPathsCheck(pluginConfig);
            ClassUtil.mergeObjectsDefaultNewObject(pluginConfig, defaultPluginConfig, PluginConfigFields.class);
            var nulls = ClassUtil.validData(PluginConfigFields.class, pluginConfig);
            if (!nulls.isEmpty())
                throw ErrorUtil.create("Unable to configurePlugin some fields are null:").append(nulls).toGradleException();
        }

        var defaultDefaultableConfig = defaultDefaultableConfig();
        if (!defaultableConfigBuilderConfigured) {
            defaultableConfig = defaultDefaultableConfig;
        } else {
            defaultableConfig = new DefaultableConfig(defaultableConfigBuilder, defaultDefaultableConfig);
            defaultableConfigBuilder = null;
            var nulls = ClassUtil.validData(defaultableConfig);
            if (!nulls.isEmpty())
                throw ErrorUtil.create("Unable to configureDefaultable some fields are null:").append(nulls).toGradleException();
        }*/
    }

/*    private void customPathsCheck(PluginConfig pluginConfig) {
        var customPaths = streamAllDirectories(pluginConfig).filter(Objects::nonNull).collect(Collectors.toList());

        if (pluginConfig.getAutomaticCleanup() == null) {
            if (customPaths.size() == 0) return;
            throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
        }
        if (pluginConfig.getAutomaticCleanup()) {
            if (customPaths.size() == 0) return;

            if (customPaths.contains(pluginConfig.getDefaultDir())) {
                if (!getProject().getLayout().getProjectDirectory().getAsFile().equals(pluginConfig.getDefaultDir().getParentFile())) {
                    PluginLogger.warn("The default directory in not in the root project and automatic cleanup is on, this might delete unwanted directory's if configured incorrectly");
                }
            }
            PluginLogger.warn("Custom global directory paths detected and automatic cleanup is on, this might delete unwanted directory's if configured incorrectly");
        }
    }*/

}
