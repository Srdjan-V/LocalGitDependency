package com.srdjanv.localgitdependency.property;

import com.srdjanv.localgitdependency.depenency.Dependency;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class CommonPropertyGetters extends CommonPropertyFields {

    public String getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    public File getDir() {
        return gitDir;
    }

    @Nullable
    public File getJavaHomeDir() {
        return javaHomeDir;
    }

    public File getPersistentFolder() {
        return persistentFolder;
    }

    public File getMavenFolder() {
        return mavenFolder;
    }

    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }
}
