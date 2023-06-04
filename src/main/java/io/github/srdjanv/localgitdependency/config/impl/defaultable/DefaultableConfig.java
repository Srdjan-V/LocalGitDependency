package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.eclipse.jgit.annotations.Nullable;

// non null data is used for you supply a custom default config, all data is otherwise nullable
@NonNullData
@SuppressWarnings("unused")
public final class DefaultableConfig extends DefaultableConfigFields {
    private final boolean custom;

    public DefaultableConfig(Builder builder) {
        this.custom = false;
        ClassUtil.instantiateObjectWithBuilder(this, builder, DefaultableConfigFields.class);
        var launcherBuilder = new Launchers.Launcher.Builder();
        if (ClosureUtil.delegateNullSafe(builder.launcher, launcherBuilder)) {
            launcher = new Launchers.Launcher(launcherBuilder);
        } else throw new NullPointerException("buildLauncher is null");
    }

    public DefaultableConfig(Builder builder, DefaultableConfig defaultable) {
        this.custom = true;
        ClassUtil.mergeObjectsDefaultReference(this, defaultable, DefaultableConfigFields.class);
        ClassUtil.mergeObjectsDefaultNewObject(this, builder, DefaultableConfigFields.class);

        var launcherBuilder = new Launchers.Launcher.Builder();
        if (ClosureUtil.delegateNullSafe(builder.launcher, launcherBuilder)) {
            launcher = new Launchers.Launcher(launcherBuilder);
        } else if (defaultable.getLauncher() != null){
            launcher = defaultable.getLauncher();
        } else throw new NullPointerException("buildLauncher is null");
    }

    public boolean isCustom() {
        return custom;
    }

    @Nullable
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    @Nullable
    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    @Nullable
    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepInitScriptUpdated;
    }

    @Nullable
    public Boolean getTryGeneratingSourceJar() {
        return tryGeneratingSourceJar;
    }

    @Nullable
    public Boolean getTryGeneratingJavaDocJar() {
        return tryGeneratingJavaDocJar;
    }

    @Nullable
    public Boolean getEnableIdeSupport() {
        return enableIdeSupport;
    }

    @Nullable
    public Boolean getRegisterDependencyRepositoryToProject() {
        return registerDependencyRepositoryToProject;
    }

    @Nullable
    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @Nullable
    public Launchers.Launcher getLauncher() {
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
