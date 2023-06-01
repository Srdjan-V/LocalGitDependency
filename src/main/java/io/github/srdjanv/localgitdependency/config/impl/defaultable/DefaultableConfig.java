package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.ClassUtil;

/**
 * Base property's used for dependency and global configuration.
 * A new dependency will inherit properties from the global configuration.
 */
@SuppressWarnings("unused")
public final class DefaultableConfig extends DefaultableConfigFields {
    private final boolean custom;

    public DefaultableConfig(Builder builder, boolean custom) {
        this.custom = custom;
        ClassUtil.instantiateObjectWithBuilder(this, builder, DefaultableConfigFields.class);
    }

    public boolean isCustom() {
        return custom;
    }

    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepInitScriptUpdated;
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

    public Closure getLauncher(){
        return launcher;
    }

    public static class Builder extends DefaultableConfigFields implements DefaultableBuilder {
        @Override
        public void keepGitUpdated(Boolean keepGitUpdated) {
            this.keepGitUpdated = keepGitUpdated;
        }

        @Override
        public void keepInitScriptUpdated(Boolean keepInitScriptUpdated) {
            this.keepInitScriptUpdated = keepInitScriptUpdated;
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
        public void enableIdeSupport(Boolean enableIdeSupport) {
            this.enableIdeSupport = enableIdeSupport;
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
        public void buildLauncher(Closure launcher) {
            this.launcher = launcher;
        }

    }

}
