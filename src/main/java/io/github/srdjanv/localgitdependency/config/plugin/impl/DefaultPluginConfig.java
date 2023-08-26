package io.github.srdjanv.localgitdependency.config.plugin.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultPluginConfig extends GroovyObjectSupport implements PluginConfig, ConfigFinalizer {
    @Inject
    public DefaultPluginConfig(Managers managers) {
        getLibsDir().convention(Constants.libsDir.apply(managers.getProject()));
        getAutomaticCleanup().convention(true);
        getKeepInitScriptUpdated().convention(true);
        getGenerateGradleTasks().convention(true);
        getGenerateGradleTasks().convention(true);
    }

    @Override
    public void finalizeProps() {

    }
}
