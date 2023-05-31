package io.github.srdjanv.localgitdependency.config.impl.plugin;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

public class PluginConfigFields {
    Boolean keepMainInitScriptUpdated;
    Boolean generateGradleTasks;
    Boolean automaticCleanup;
    Boolean keepGitUpdated;
    Boolean keepDependencyInitScriptUpdated;
    File gitDir;
    File persistentDir;
    File mavenDir;
    Dependency.Type dependencyType;
    Boolean tryGeneratingSourceJar;
    Boolean tryGeneratingJavaDocJar;
    Boolean enableIdeSupport;
    Boolean registerDependencyRepositoryToProject;
    Integer gradleDaemonMaxIdleTime;
}
