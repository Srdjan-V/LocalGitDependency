package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherConfig;
import io.github.srdjanv.localgitdependency.project.Managers;

import javax.inject.Inject;

public abstract class DefaultLauncherConfig extends GroovyObjectSupport implements LauncherConfig, ConfigFinalizer {
    @Inject
    public DefaultLauncherConfig(Managers managers) {
        getStartup().convention(managers.getProject().getObjects().newInstance(DefaultLaunchers.Startup.class, managers));
        getProbe().convention(managers.getProject().getObjects().newInstance(DefaultLaunchers.Probe.class, managers));
        getBuild().convention(managers.getProject().getObjects().newInstance(DefaultLaunchers.Build.class, managers));

        var defaultable = managers.getConfigManager().getDefaultableConfig().getBuildLauncher().get();
        getGradleDaemonMaxIdleTime().convention(managers.getProject().provider(() -> defaultable.getGradleDaemonMaxIdleTime().get()));
        getForwardOutput().convention(managers.getProject().provider(() -> defaultable.getForwardOutput().get()));
    }

    @Override
    public void finalizeProps() {

    }
}
