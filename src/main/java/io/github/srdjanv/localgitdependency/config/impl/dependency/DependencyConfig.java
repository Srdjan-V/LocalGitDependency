package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyBuilder;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfigFields;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Property's that only a dependency can have
 */
public final class DependencyConfig extends DependencyConfigFields {

    public DependencyConfig(Builder builder, DefaultableConfig defaultableConfig) {
        ClassUtil.mergeObjects(this, defaultableConfig, DefaultableConfigFields.class);
        ClassUtil.mergeObjects(this, builder, DependencyConfigFields.class);
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getTarget() {
        return target;
    }

    @Nullable
    public GitInfo.TargetType getTargetType() {
        return targetType;
    }

    @Nullable
    public String getConfiguration() {
        return configuration;
    }

    @Nullable
    public Closure[] getConfigurations() {
        return configurations;
    }

    @Nullable
    public Closure[] getMappings() {
        return mappings;
    }

    @Nullable
    public Closure getLauncher() {
        return launcher;
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
    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    @Nullable
    public Boolean getKeepInitScriptUpdated() {
        return keepInitScriptUpdated;
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
    public Boolean getGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @Nullable
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    public static class Builder extends DependencyConfigFields implements DependencyBuilder {
        public Builder(String url) {
            this.url = url;
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void configuration(Closure... configurations) {
            this.configurations = configurations;
        }

        @Override
        public void mapSourceSets(Closure... mappings) {
            this.mappings = mappings;
        }

        @Override
        public void buildLauncher(Closure launcher) {
            this.launcher = launcher;
        }

        @Override
        public void name(String name) {
            this.name = name;
        }

        @Override
        public void commit(String commit) {
            targetType = GitInfo.TargetType.COMMIT;
            this.target = commit;
        }

        @Override
        public void branch(String branch) {
            targetType = GitInfo.TargetType.BRANCH;
            this.target = branch;
        }

        @Override
        public void tag(String tag) {
            targetType = GitInfo.TargetType.TAG;
            this.target = tag;
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
        public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
            this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
        }
    }
}
