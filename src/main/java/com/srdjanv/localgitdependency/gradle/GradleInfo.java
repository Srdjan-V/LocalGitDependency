package com.srdjanv.localgitdependency.gradle;

import org.jetbrains.annotations.NotNull;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.property.Property;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GradleInfo {
    private final Dependency dependency;
    private final File initScript;
    private final File javaHome;
    private final boolean keepDependencyInitScriptUpdated;
    private final boolean tryGeneratingSourceJar;
    private final boolean tryGeneratingJavaDocJar;

    public GradleInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.keepDependencyInitScriptUpdated = dependencyProperty.getKeepDependencyInitScriptUpdated();
        this.initScript = Constants.persistentInitScript.apply(dependencyProperty.getPersistentFolder(), dependency.getName());
        this.javaHome = dependencyProperty.getJavaHomeDir();
        this.tryGeneratingSourceJar = dependencyProperty.getTryGeneratingSourceJar();
        this.tryGeneratingJavaDocJar = dependencyProperty.getTryGeneratingJavaDocJar();
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
}
