package com.srdjanv.localgitdependency.gradle;

import org.jetbrains.annotations.NotNull;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.property.Property;

import java.io.File;

public class GradleInfo {
    private final Dependency dependency;
    private final File initScript;
    private final boolean keepDependencyInitScriptUpdated;

    public GradleInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.keepDependencyInitScriptUpdated = dependencyProperty.getKeepDependencyInitScriptUpdated();
        this.initScript = Constants.persistentInitScript.apply(dependencyProperty.getPersistentFolder(), dependency.getName());
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @NotNull
    public File getInitScript() {
        return initScript;
    }

    public boolean isKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }
}
