package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
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

    public GradleInfo(Managers managers, DependencyConfig dependencyConfig, Dependency dependency) {
        this.dependency = dependency;
        this.launchers = GradleLaunchers.build(dependency, dependencyConfig);
        this.keepInitScriptUpdated = dependencyConfig.getKeepInitScriptUpdated().get();
        // TODO: 25/08/2023 test 
        this.initScript = Constants.persistentInitScript.apply(
                Constants.lgdDir.apply(managers.getProject()).getAsFile(),
                dependency.getName());
        this.tryGeneratingSourceJar = dependencyConfig.getTryGeneratingSourceJar().get();
        this.tryGeneratingJavaDocJar = dependencyConfig.getTryGeneratingJavaDocJar().get();
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
