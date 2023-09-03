package io.github.srdjanv.localgitdependency.config.plugin.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import javax.inject.Inject;

public abstract class DefaultPluginConfig extends GroovyObjectSupport implements PluginConfig, ConfigFinalizer {
    @Inject
    public DefaultPluginConfig(Managers managers) {
        getLibsDir().convention(FileUtil.getLibsDir(managers.getProject()));
        getAutomaticCleanup().convention(managers.getProject().provider(() -> {
            return getLibsDir().get().equals(FileUtil.getLibsDir(managers.getProject()));
        }));
        getKeepInitScriptUpdated().convention(true);
        getGenerateGradleTasks().convention(true);
        getDisablePluginExecution().convention(false);
    }

    @Override
    public void finalizeProps() {
        ClassUtil.finalizeProperties(this, PluginConfig.class);
    }
}
