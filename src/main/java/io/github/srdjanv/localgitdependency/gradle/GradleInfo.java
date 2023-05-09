package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public class GradleInfo {
    private final Dependency dependency;
    private final File initScript;
    private final File javaHome;
    private final boolean keepDependencyInitScriptUpdated;
    private final boolean tryGeneratingSourceJar;
    private final boolean tryGeneratingJavaDocJar;
    private final int gradleDaemonMaxIdleTime;
    private final String[] startupTasks;

    public GradleInfo(DependencyProperty dependencyConfig, Dependency dependency) {
        this.dependency = dependency;
        this.keepDependencyInitScriptUpdated = dependencyConfig.getKeepDependencyInitScriptUpdated();
        this.initScript = Constants.persistentInitScript.apply(dependencyConfig.getPersistentDir(), dependency.getName());
        this.javaHome = dependencyConfig.getJavaHomeDir();
        this.tryGeneratingSourceJar = dependencyConfig.getTryGeneratingSourceJar();
        this.tryGeneratingJavaDocJar = dependencyConfig.getTryGeneratingJavaDocJar();
        this.gradleDaemonMaxIdleTime = dependencyConfig.getGradleDaemonMaxIdleTime();
        this.startupTasks = dependencyConfig.getStartupTasks();
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @NotNull
    public File getInitScript() {
        return initScript;
    }

    @Nullable
    public File getJavaHome() {
        return javaHome;
    }

    public boolean isKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }

    public boolean isTryGeneratingSourceJar() {
        return tryGeneratingSourceJar;
    }

    public boolean isTryGeneratingJavaDocJar() {
        return tryGeneratingJavaDocJar;
    }

    public int getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    @Nullable
    public String[] getStartupTasks() {
        return startupTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradleInfo gitInfo = (GradleInfo) o;
        return Objects.equals(gitInfo.getDependency().getName(), getDependency().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency.getName());
    }
}
