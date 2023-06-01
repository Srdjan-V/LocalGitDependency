package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.depenency.Dependency;

public abstract class DefaultableConfigFields {
    protected DefaultableConfigFields() {
    }
    protected Boolean keepGitUpdated;
    protected Boolean keepInitScriptUpdated;
    protected Dependency.Type dependencyType;
    protected Boolean tryGeneratingSourceJar;
    protected Boolean tryGeneratingJavaDocJar;
    protected Boolean enableIdeSupport;
    protected Boolean registerDependencyRepositoryToProject;
    protected Boolean generateGradleTasks;
    protected Closure launcher;

}
