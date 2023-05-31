package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public final class GradleInfo {
    private final Dependency dependency;
    private final GradleLaunchers launchers;
    private final File initScript;
    private final boolean keepInitScriptUpdated;
    private final boolean tryGeneratingSourceJar;
    private final boolean tryGeneratingJavaDocJar;
    private final int gradleDaemonMaxIdleTime;

    public GradleInfo(DependencyConfig dependencyConfig, Dependency dependency) {
        this.dependency = dependency;
        this.launchers = GradleLaunchers.build(dependencyConfig);
        this.keepInitScriptUpdated = dependencyConfig.getKeepInitScriptUpdated();
        this.initScript = Constants.persistentInitScript.apply(dependencyConfig.getPersistentDir(), dependency.getName());
        this.tryGeneratingSourceJar = dependencyConfig.getTryGeneratingSourceJar();
        this.tryGeneratingJavaDocJar = dependencyConfig.getTryGeneratingJavaDocJar();
        this.gradleDaemonMaxIdleTime = dependencyConfig.getGradleDaemonMaxIdleTime();
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @NotNull
    public File getInitScript() {
        return initScript;
    }
    public boolean isKeepInitScriptUpdated() {
        return keepInitScriptUpdated;
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

    @NotNull
    public GradleLaunchers getLaunchers() {
        return launchers;
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
