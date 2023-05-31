package io.github.srdjanv.localgitdependency.config.impl.plugin;

import io.github.srdjanv.localgitdependency.config.plugin.PluginBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public Boolean getKeepMainInitScriptUpdated() {
        return keepMainInitScriptUpdated;
    }

    @NotNull
    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @NotNull
    public Boolean getAutomaticCleanup() {
        return automaticCleanup;
    }

    @NotNull
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    @NotNull
    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }

    @NotNull
    public File getDefaultDir() {
        return defaultDir;
    }

    @NotNull
    public File getGitDir() {
        return gitDir;
    }

    @NotNull
    public File getPersistentDir() {
        return persistentDir;
    }

    @NotNull
    public File getMavenDir() {
        return mavenDir;
    }

    @NotNull
    public Dependency.Type getDependencyType() {
        return dependencyType;
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
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    public static class Builder extends PluginConfigFields implements PluginBuilder {
        public Builder() {
        }

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
        public void defaultDir(File defaultDir) {
            this.defaultDir = defaultDir;
        }

        @Override
        public void defaultDir(String defaultDir) {
            if (defaultDir != null)
                this.defaultDir = new File(defaultDir);
        }

        @Override
        public void gitDir(File dir) {
            this.gitDir = FileUtil.configureFilePath(defaultDir, dir);
        }

        @Override
        public void gitDir(String dir) {
            this.gitDir = FileUtil.configureFilePath(defaultDir, dir);
        }

        @Override
        public void persistentDir(File persistentDir) {
            this.persistentDir = FileUtil.configureFilePath(defaultDir, persistentDir);
        }

        @Override
        public void persistentDir(String persistentDir) {
            this.persistentDir = FileUtil.configureFilePath(defaultDir, persistentDir);
        }

        @Override
        public void mavenDir(File mavenDir) {
            this.mavenDir = FileUtil.configureFilePath(defaultDir, mavenDir);
        }

        @Override
        public void mavenDir(String mavenDir) {
            this.mavenDir = FileUtil.configureFilePath(defaultDir, mavenDir);
        }

    }
}
