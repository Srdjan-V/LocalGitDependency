package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyBuilder;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableConfigFields;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Property's that only a dependency can have
 */
public final class DependencyConfig extends DependencyConfigFields {

    public DependencyConfig(Builder builder, DefaultableConfig defaultableConfig) {
        ClassUtil.mergeObjectsDefaultReference(this, defaultableConfig, DefaultableConfigFields.class);
        ClassUtil.mergeObjectsDefaultNewObject(this, builder, DependencyConfigFields.class);

        if (builder.launcher != null) {
            var launcherBuilder = new Launchers.Launcher.Builder();
            ClosureUtil.delegate(builder.launcher, launcherBuilder);
            launcher = new Launchers.Launcher(launcherBuilder, launcher);
        }

        if (builder.configurations != null) {
            List<ConfigurationConfig> configurationConfigList = new ArrayList<>();
            for (Closure closure : builder.configurations) {
                var configurationConfig = new ConfigurationConfig.Builder();
                if (ClosureUtil.delegateNullSafe(closure, configurationConfig)) {
                    configurationConfigList.add(new ConfigurationConfig(configurationConfig));
                } else throw new GradleException("Null provided as a configuration closure");
            }
            this.configurationConfig = configurationConfigList;
        }

        if (builder.subConfigurations != null) {
            List<SubConfigurationConfig> subConfigurationConfigList = new ArrayList<>();
            for (Closure closure : builder.subConfigurations) {
                var configurationConfig = new SubConfigurationConfig.Builder();
                if (ClosureUtil.delegateNullSafe(closure, configurationConfig)) {
                    subConfigurationConfigList.add(new SubConfigurationConfig(configurationConfig));
                } else throw new GradleException("Null provided as a configuration closure");
            }
            this.subConfigurationConfig = subConfigurationConfigList;
        }


        if (builder.mappings != null) {
            List<SourceSetMapperConfig> sourceSetMapperConfigList = new ArrayList<>();
            for (Closure closure : builder.mappings) {
                var sourceSetMapperConfig = new SourceSetMapperConfig.Builder();
                if (ClosureUtil.delegateNullSafe(closure, sourceSetMapperConfig)) {
                    sourceSetMapperConfigList.add(new SourceSetMapperConfig(sourceSetMapperConfig));
                } else throw new GradleException("Null provided as a mappings closure");
            }
            this.sourceSetMapperConfig = sourceSetMapperConfigList;
        }
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
    public List<ConfigurationConfig> getConfigurations() {
        return configurationConfig;
    }

    @Nullable
    public List<SubConfigurationConfig> getSubConfigurations() {
        return subConfigurationConfig;
    }

    @Nullable
    public List<SourceSetMapperConfig> getMappings() {
        return sourceSetMapperConfig;
    }

    @NotNull
    public Launchers.Launcher getLauncher() {
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

    public static class Builder extends DependencyConfigFields implements DependencyBuilder {
        protected Closure[] configurations;
        protected Closure[] subConfigurations;
        protected Closure[] mappings;
        protected Closure launcher;

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
        public void subConfiguration(Closure... configurations) {
            subConfigurations = configurations;
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
        public void gitDir(Object dir) {
            this.gitDir = FileUtil.toFile(dir, "gitDir");
        }

        @Override
        public void persistentDir(Object persistentDir) {
            this.persistentDir = FileUtil.toFile(persistentDir, "persistentDir");
        }

        @Override
        public void mavenDir(Object mavenDir) {
            this.mavenDir = FileUtil.toFile(mavenDir, "mavenDir");
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
    }
}
