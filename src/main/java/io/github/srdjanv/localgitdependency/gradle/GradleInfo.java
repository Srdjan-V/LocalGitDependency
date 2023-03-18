package io.github.srdjanv.localgitdependency.gradle;

import org.jetbrains.annotations.NotNull;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.Property;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GradleInfo {
    private final Dependency dependency;
    private final File initScript;
    private final File javaHome;
    private final boolean keepDependencyInitScriptUpdated;
    private final boolean tryGeneratingSourceJar;
    private final boolean tryGeneratingJavaDocJar;
    private final int gradleDaemonMaxIdleTime;

    public GradleInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.keepDependencyInitScriptUpdated = dependencyProperty.getKeepDependencyInitScriptUpdated();
        this.initScript = Constants.persistentInitScript.apply(dependencyProperty.getPersistentDir(), dependency.getName());
        this.javaHome = dependencyProperty.getJavaHomeDir();
        this.tryGeneratingSourceJar = dependencyProperty.getTryGeneratingSourceJar();
        this.tryGeneratingJavaDocJar = dependencyProperty.getTryGeneratingJavaDocJar();
        this.gradleDaemonMaxIdleTime = dependencyProperty.getGradleDaemonMaxIdleTime();
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
}
