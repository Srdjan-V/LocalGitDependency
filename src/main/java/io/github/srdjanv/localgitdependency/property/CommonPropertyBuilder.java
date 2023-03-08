package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

//Base property's for dependency's global configurations
@SuppressWarnings("unused")
public abstract class CommonPropertyBuilder extends CommonPropertyFields {
    CommonPropertyBuilder() {
    }

    public void configuration(String configuration) {
        this.configuration = configuration;
    }

    public void keepGitUpdated(Boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }

    public void keepDependencyInitScriptUpdated(Boolean keepDependencyInitScriptUpdated) {
        this.keepDependencyInitScriptUpdated = keepDependencyInitScriptUpdated;
    }

    public void gitDir(File dir) {
        this.gitDir = dir;
    }

    public void gitDir(String dir) {
        this.gitDir = new File(dir);
    }

    public void javaHomeDir(File javaHomeDir) {
        this.javaHomeDir = javaHomeDir;
    }

    public void javaHomeDir(String javaHomeDir) {
        this.javaHomeDir = new File(javaHomeDir);
    }

    public void persistentFolder(File initScript) {
        this.persistentFolder = initScript;
    }

    public void persistentFolder(String initScript) {
        this.persistentFolder = new File(initScript);
    }

    public void mavenFolder(File mavenFolder) {
        this.mavenFolder = mavenFolder;
    }

    public void mavenFolder(String mavenFolder) {
        this.mavenFolder = new File(mavenFolder);
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
