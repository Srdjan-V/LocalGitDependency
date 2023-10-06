package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import java.util.Collections;
import javax.inject.Inject;

public abstract class DefaultDefaultableConfig extends GroovyObjectSupport
        implements DefaultableConfig, ConfigFinalizer {
    @Inject
    public DefaultDefaultableConfig(Managers managers) {
        getKeepGitUpdated().convention(true);
        getForceGitUpdate().convention(false);
        getCloneGitSubmodules().convention(false);
        getKeepInitScriptUpdated().convention(true);
        getGenerateGradleTasks().convention(true);
        getTryGeneratingSourceJar().convention(false);
        getTryGeneratingJavaDocJar().convention(false);
        getDependencyTags().convention(Collections.emptyList());
        getBuildLauncher()
                .convention(managers.getProject()
                        .getObjects()
                        .newInstance(DefaultDefaultableLauncherConfig.class, managers));
    }

    @Override
    public void finalizeProps() {
        ClassUtil.finalizeProperties(this, DefaultableConfig.class);
        ((DefaultDefaultableLauncherConfig) getBuildLauncher().get()).finalizeProps();
    }
}
