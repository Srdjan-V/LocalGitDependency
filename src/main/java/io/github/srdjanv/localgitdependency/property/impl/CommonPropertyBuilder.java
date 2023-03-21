package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.CommonBuilder;

import java.io.File;

/**
 * Base property's used for dependency and global configuration.
 * A new dependency will inherit properties from the global configuration.
 */
@SuppressWarnings("unused")
public abstract class CommonPropertyBuilder extends CommonPropertyFields implements CommonBuilder {
    CommonPropertyBuilder() {
    }

    @Override
    public void configuration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public void keepGitUpdated(Boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }
    @Override
    public void keepDependencyInitScriptUpdated(Boolean keepDependencyInitScriptUpdated) {
        this.keepDependencyInitScriptUpdated = keepDependencyInitScriptUpdated;
    }

    @Override
    public void gitDir(File dir) {
        this.gitDir = dir;
    }

    @Override
    public void gitDir(String dir) {
        this.gitDir = new File(dir);
    }

    @Override
    public void javaHomeDir(File javaHomeDir) {
        this.javaHomeDir = javaHomeDir;
    }

    @Override
    public void javaHomeDir(String javaHomeDir) {
        this.javaHomeDir = new File(javaHomeDir);
    }

    @Override
    public void persistentDir(File persistentDir) {
        this.persistentDir = persistentDir;
    }

    @Override
    public void persistentDir(String persistentDir) {
        this.persistentDir = new File(persistentDir);
    }


    @Override
    public void mavenDir(File mavenDir) {
        this.mavenDir = mavenDir;
    }

    @Override
    public void mavenDir(String mavenDir) {
        this.mavenDir = new File(mavenDir);
    }

    @Override
    public void dependencyType(Dependency.Type dependencyType) {
        this.dependencyType = dependencyType;
    }

    @Override
    public void tryGeneratingSourceJar(Boolean tryGeneratingSourceJar) {
        this.tryGeneratingSourceJar = tryGeneratingSourceJar;
    }

    @Override
    public void tryGeneratingJavaDocJar(Boolean tryGeneratingJavaDocJar) {
        this.tryGeneratingJavaDocJar = tryGeneratingJavaDocJar;
    }

    @Override
    public void addDependencySourcesToProject(Boolean addDependencySourcesToProject) {
        this.addDependencySourcesToProject = addDependencySourcesToProject;
    }

    @Override
    public void registerDependencyToProject(Boolean registerDependencyToProject) {
        this.registerDependencyToProject = registerDependencyToProject;
    }

    @Override
    public void registerDependencyRepositoryToProject(Boolean registerDependencyRepositoryToProject) {
        this.registerDependencyRepositoryToProject = registerDependencyRepositoryToProject;
    }


    @Override
    public void generateGradleTasks(Boolean generateGradleTasks) {
        this.generateGradleTasks = generateGradleTasks;
    }

    @Override
    public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
        this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
    }

}
