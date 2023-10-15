package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultLaunchers;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GradleLaunchers {
    public static GradleLaunchers build(Dependency dependency, DependencyConfig dependencyConfig) {
        return new GradleLaunchers(dependency, dependencyConfig);
    }

    private final File executable;
    private final Integer gradleDaemonMaxIdleTime;
    private final DefaultLaunchers.Startup startup;
    private final DefaultLaunchers.Probe probe;
    private final DefaultLaunchers.Build build;

    private GradleLaunchers(Dependency dependency, DependencyConfig dependencyConfig) {
        var launcherConfig = dependencyConfig.getBuildLauncher();
        if (launcherConfig.getExecutable().isPresent()) {
            executable = FileUtil.toFile(launcherConfig.getExecutable().get(), "getExecutable");
        } else executable = null;
        gradleDaemonMaxIdleTime = launcherConfig.getGradleDaemonMaxIdleTime().get();

        startup = (DefaultLaunchers.Startup) launcherConfig.getStartup();
        probe = (DefaultLaunchers.Probe) launcherConfig.getProbe();
        build = (DefaultLaunchers.Build) launcherConfig.getBuild();
    }

    @Nullable public File getExecutable() {
        return executable;
    }

    @NotNull public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    @NotNull public DefaultLaunchers.Startup getStartup() {
        return startup;
    }

    @NotNull public DefaultLaunchers.Probe getProbe() {
        return probe;
    }

    @NotNull public DefaultLaunchers.Build getBuild() {
        return build;
    }
}
