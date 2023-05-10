package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class CommonPropertyGetters extends CommonPropertyFields {
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    public File getGitDir() {
        return gitDir;
    }

    @Nullable
    public File getJavaHomeDir() {
        return javaHomeDir;
    }

    public File getPersistentDir() {
        return persistentDir;
    }

    public File getMavenDir() {
        return mavenDir;
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

    public Boolean getEnableIdeSupport() {
        return enableIdeSupport;
    }

    public Boolean getRegisterDependencyRepositoryToProject() {
        return registerDependencyRepositoryToProject;
    }

    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }
}
