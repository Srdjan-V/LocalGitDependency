package io.github.srdjanv.localgitdependency.config.dependency.impl;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultDefaultableConfig implements DefaultableConfig {
    @Inject
    public DefaultDefaultableConfig(Managers managers) {
        keepGitUpdated().convention(true);
        keepInitScriptUpdated().convention(true);
        generateGradleTasks().convention(true);
        tryGeneratingSourceJar().convention(false);
        tryGeneratingJavaDocJar().convention(false);
        registerDependencyRepositoryToProject().convention(true);
        buildLauncher().convention(managers.getProject().getObjects().newInstance(DefaultDefaultableLauncherConfig.class, managers));
    }
}
