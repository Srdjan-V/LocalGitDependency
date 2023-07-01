package io.github.srdjanv.localgitdependency.config.impl.plugin;

import io.github.srdjanv.localgitdependency.config.plugin.PluginBuilder;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.jetbrains.annotations.NotNull;

import java.io.File;


@NonNullData
public final class PluginConfig extends PluginConfigFields {

    public PluginConfig(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, PluginConfigFields.class);

        if (builder.newDefaultDir != null) {
            var newFile = FileUtil.toFile(builder.newDefaultDir, "defaultDir");
            if (newFile.isAbsolute()) {
                this.defaultDir = newFile;
            } else {
                this.defaultDir = new File(this.defaultDir.getParentFile(), newFile.getName());
            }
        }

        if (builder.gitDir != null)
            this.gitDir = FileUtil.configureFilePath(defaultDir, FileUtil.toFile(builder.gitDir, "gitDir"));

        if (builder.persistentDir != null)
            this.persistentDir = FileUtil.configureFilePath(defaultDir, FileUtil.toFile(builder.persistentDir, "persistentDir"));

        if (builder.mavenDir != null)
            this.mavenDir = FileUtil.configureFilePath(defaultDir, FileUtil.toFile(builder.mavenDir, "mavenDir"));
    }

    @NotNull
    public Boolean getKeepInitScriptUpdated() {
        return keepInitScriptUpdated;
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

    public static class Builder extends PluginConfigFields implements PluginBuilder {
        private Object newDefaultDir;
        private Object gitDir;
        private Object persistentDir;
        private Object mavenDir;

        public Builder(File defaultDir) {
            this.defaultDir = defaultDir;
        }

        @Override
        public void keepInitScriptUpdated(Boolean keepInitScriptUpdated) {
            this.keepInitScriptUpdated = keepInitScriptUpdated;
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
        public void defaultDir(Object defaultDir) {
            this.newDefaultDir = defaultDir;
        }

        @Override
        public void gitDir(Object dir) {
            this.gitDir = dir;
        }

        @Override
        public void persistentDir(Object persistentDir) {
            this.persistentDir = persistentDir;
        }

        @Override
        public void mavenDir(Object mavenDir) {
            this.mavenDir = mavenDir;
        }

    }
}
