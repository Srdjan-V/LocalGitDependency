package com.srdjanv.localgitdependency.property;

import com.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

//Base property's for dependency's global configurations
public abstract class CommonPropertyBuilder extends CommonPropertyFields {
    CommonPropertyBuilder() {
    }

    public void configuration(String configuration) {
        this.configuration = configuration;
    }

    public void keepGitUpdated(boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }

    public void keepDependencyInitScriptUpdated(boolean keepDependencyInitScriptUpdated) {
        this.keepDependencyInitScriptUpdated = keepDependencyInitScriptUpdated;
    }

    public void gitDir(File dir) {
        this.gitDir = dir;
    }
    public void javaHomeDir(File javaHomeDir) {
        this.javaHomeDir = javaHomeDir;
    }
    public void persistentFolder(File initScript) {
        this.persistentFolder = initScript;
    }

    public void mavenFolder(File mavenFolder) {
        this.mavenFolder = mavenFolder;
    }

    public void dependencyType(Dependency.Type dependencyType) {
        this.dependencyType = dependencyType;
    }

    public void tryGeneratingSourceJar(Boolean tryGeneratingSourceJar) {
        this.tryGeneratingSourceJar = tryGeneratingSourceJar;
    }

    public void tryGeneratingJavaDocJar(Boolean tryGeneratingJavaDocJar) {
        this.tryGeneratingJavaDocJar = tryGeneratingJavaDocJar;
    }

}
