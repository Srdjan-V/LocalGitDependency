package io.github.srdjanv.localgitdependency.config.impl.plugin;

import io.github.srdjanv.localgitdependency.config.plugin.PluginBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Property's used for global configuration
 */
public final class PluginConfig extends PluginConfigFields {
    private final boolean custom;

    public PluginConfig(Builder builder, boolean custom) {
        this.custom = custom;
        BuilderUtil.instantiateObjectWithBuilder(this, builder, PluginConfigFields.class);
    }

    public boolean isCustom() {
        return custom;
    }

    @Nullable
    public Boolean getKeepMainInitScriptUpdated() {
        return keepMainInitScriptUpdated;
    }

    @Nullable
    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @Nullable
    public Boolean getAutomaticCleanup() {
        return automaticCleanup;
    }

    @Nullable
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    @Nullable
    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }

    @Nullable
    public File getGitDir() {
        return gitDir;
    }

    @Nullable
    public File getPersistentDir() {
        return persistentDir;
    }

    @Nullable
    public File getMavenDir() {
        return mavenDir;
    }

    @Nullable
    public Dependency.Type getDependencyType() {
        return dependencyType;
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
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    public static class Builder extends PluginConfigFields implements PluginBuilder {

        @Override
        public void keepInitScriptUpdated(Boolean keepInitScriptUpdated) {
            this.keepMainInitScriptUpdated = keepInitScriptUpdated;
        }

        @Override
        public void generateGradleTasks(Boolean generateGradleTasks) {
            this.generateGradleTasks = generateGradleTasks;
        }

        @Override
        public void automaticCleanup(Boolean automaticCleanup) {
            this.automaticCleanup = automaticCleanup;
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

    }
}
