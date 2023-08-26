package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;
import java.util.Collections;

public abstract class DefaultDefaultableConfig extends GroovyObjectSupport implements DefaultableConfig, ConfigFinalizer {
    @Inject
    public DefaultDefaultableConfig(Managers managers) {
        getKeepGitUpdated().convention(true);
        getKeepInitScriptUpdated().convention(true);
        getGenerateGradleTasks().convention(true);
        getTryGeneratingSourceJar().convention(false);
        getTryGeneratingJavaDocJar().convention(false);
        getRegisterDependencyRepositoryToProject().convention(true);
        getBuildTargets().convention(Collections.singleton(Dependency.Type.JarFlatDir));
        getBuildLauncher().convention(managers.getProject().getObjects().newInstance(DefaultDefaultableLauncherConfig.class, managers));
    }

    @Override
    public void finalizeProps() {

    }
}
