package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherConfig;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import javax.inject.Inject;

public abstract class DefaultLauncherConfig extends GroovyObjectSupport implements LauncherConfig, ConfigFinalizer {
    private final Launchers.Startup startup;
    private final Launchers.Probe probe;
    private final Launchers.Build build;

    @Inject
    public DefaultLauncherConfig(Managers managers) {
        var defaultable = managers.getConfigManager()
                .getDefaultableConfig()
                .getBuildLauncher()
                .get();
        getGradleDaemonMaxIdleTime()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getGradleDaemonMaxIdleTime().get()));
        getForwardOutput()
                .convention(managers.getProject()
                        .provider(() -> defaultable.getForwardOutput().get()));

        startup = managers.getProject().getObjects().newInstance(DefaultLaunchers.Startup.class, managers);
        probe = managers.getProject().getObjects().newInstance(DefaultLaunchers.Probe.class, managers);
        build = managers.getProject().getObjects().newInstance(DefaultLaunchers.Build.class, managers);
    }

    @Override
    public Launchers.Startup getStartup() {
        return startup;
    }

    @Override
    public Launchers.Probe getProbe() {
        return probe;
    }

    @Override
    public Launchers.Build getBuild() {
        return build;
    }

    @Override
    public void finalizeProps() {
        ClassUtil.finalizeProperties(this, LauncherConfig.class);
        ((DefaultLaunchers.Startup) getStartup()).finalizeProps();
        ((DefaultLaunchers.Probe) getProbe()).finalizeProps();
        ((DefaultLaunchers.Build) getBuild()).finalizeProps();
    }
}
