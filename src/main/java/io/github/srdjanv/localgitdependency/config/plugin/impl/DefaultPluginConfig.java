package io.github.srdjanv.localgitdependency.config.plugin.impl;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultPluginConfig implements PluginConfig {
    @Inject
    public DefaultPluginConfig(Managers managers) {
        libsDir().convention(Constants.libsDir.apply(managers.getProject()));
        automaticCleanup().convention(true);
        keepInitScriptUpdated().convention(true);
        generateGradleTasks().convention(true);
        generateGradleTasks().convention(true);
    }
}
