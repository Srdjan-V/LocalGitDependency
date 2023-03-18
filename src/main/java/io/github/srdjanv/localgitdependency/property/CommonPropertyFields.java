package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyFields {
    CommonPropertyFields() {
    }

    String configuration;
    Boolean keepGitUpdated;
    Boolean keepDependencyInitScriptUpdated;
    File gitDir;
    File javaHomeDir;
    File persistentDir;
    File mavenDir;
    Dependency.Type dependencyType;
    Boolean tryGeneratingSourceJar;
    Boolean tryGeneratingJavaDocJar;
    Boolean addDependencySourcesToProject;
    Boolean registerDependencyToProject;
    Boolean registerDependencyRepositoryToProject;
    Boolean generateGradleTasks;
}
