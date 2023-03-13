package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class CommonPropertyGetters extends CommonPropertyFields {

    public String getConfiguration() {
        return configuration;
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

    public Boolean getTryGeneratingSourceJar() {
        return tryGeneratingSourceJar;
    }

    public Boolean getTryGeneratingJavaDocJar() {
        return tryGeneratingJavaDocJar;
    }

    public Boolean getAddDependencySourcesToProject() {
        return addDependencySourcesToProject;
    }

    public Boolean getRegisterDependencyToProject() {
        return registerDependencyToProject;
    }

    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }
}
