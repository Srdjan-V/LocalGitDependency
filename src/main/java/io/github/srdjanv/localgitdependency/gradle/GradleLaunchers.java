package io.github.srdjanv.localgitdependency.gradle;


import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultLaunchers;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class GradleLaunchers implements ConfigFinalizer {
    public static GradleLaunchers build(Dependency dependency, DependencyConfig dependencyConfig) {
        return new GradleLaunchers(dependency, dependencyConfig);
    }

    private final File executable;
    private final Integer gradleDaemonMaxIdleTime;
    private final DefaultLaunchers.Startup startup;
    private final DefaultLaunchers.Probe probe;
    private final DefaultLaunchers.Build build;

    private GradleLaunchers(Dependency dependency, DependencyConfig dependencyConfig) {
        var launcherConfig = dependencyConfig.getBuildLauncher().get();
        startup = (DefaultLaunchers.Startup) launcherConfig.getStartup().get();
        startup.getDependencyProperty().set(dependency);

        probe = (DefaultLaunchers.Probe) launcherConfig.getProbe().get();
        probe.getDependencyProperty().set(dependency);

        build = (DefaultLaunchers.Build) launcherConfig.getBuild().get();
        build.getDependencyProperty().set(dependency);
    }

    @Override
    public void finalizeProps() {

    }

    @Nullable
    public File getExecutable() {
        return executable;
    }

    @NotNull
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    @NotNull
    public Launchers.Startup getStartup() {
        return startup;
    }

    @NotNull
    public Launchers.Probe getProbe() {
        return probe;
    }

    @NotNull
    public Launchers.Build getBuild() {
        return build;
    }


}
