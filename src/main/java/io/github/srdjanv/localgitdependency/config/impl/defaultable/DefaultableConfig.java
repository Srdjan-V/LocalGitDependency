package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.jetbrains.annotations.NotNull;

@NonNullData
@SuppressWarnings("unused")
public final class DefaultableConfig extends DefaultableConfigFields {
    private DefaultableLauncherConfig launcher;

    //defaultBuilder only constructor
    public DefaultableConfig(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, DefaultableConfigFields.class);
        var launcherBuilder = new DefaultableLauncherConfig.Builder();
        ClosureUtil.delegateNullSafe(builder.launcher, launcherBuilder);
        launcher = new DefaultableLauncherConfig(launcherBuilder);
    }

    //merging constructor
    public DefaultableConfig(Builder builder, DefaultableConfig defaultConfig) {
        ClassUtil.instantiateObjectWithBuilder(this, defaultConfig, DefaultableConfigFields.class);
        ClassUtil.mergeObjectsDefaultNewObject(this, builder, DefaultableConfigFields.class);
        if (builder.launcher != null) {
            var launcherBuilder = new DefaultableLauncherConfig.Builder();
            ClosureUtil.delegate(builder.launcher, launcherBuilder);
            launcher = new DefaultableLauncherConfig(launcherBuilder, defaultConfig.launcher);
        }
    }

    @NotNull
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    @NotNull
    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    @NotNull
    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepInitScriptUpdated;
    }

    @NotNull
    public Boolean getTryGeneratingSourceJar() {
        return tryGeneratingSourceJar;
    }

    @NotNull
    public Boolean getTryGeneratingJavaDocJar() {
        return tryGeneratingJavaDocJar;
    }

    @NotNull
    public Boolean getEnableIdeSupport() {
        return enableIdeSupport;
    }

    @NotNull
    public Boolean getRegisterDependencyRepositoryToProject() {
        return registerDependencyRepositoryToProject;
    }

    @NotNull
    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @NotNull
    public DefaultableLauncherConfig getLauncher() {
        return launcher;
    }

    public static class Builder extends DefaultableConfigFields implements DefaultableBuilder {
        private Closure launcher;

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
