package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dependency {
    private final String name;
    private final List<Configuration> configurations;
    private final List<SourceSetMapper> mappers;
    private final boolean ideSupport;
    private final boolean shouldRegisterRepository;
    private final boolean generateGradleTasks;
    private final Type dependencyType;
    private final File mavenFolder;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;
    private final PersistentInfo persistentInfo;

    public Dependency(Managers managers, DependencyConfig dependencyConfig) {
        ErrorUtil errorBuilder = ErrorUtil.create("Git dependency errors:");
        this.name = dependencyConfig.getName() == null ? getNameFromUrl(dependencyConfig.getUrl()) : dependencyConfig.getName();
        if (this.name == null) {
            errorBuilder.append("DependencyConfig: 'name' is null");
        }

        this.configurations = Configuration.build(dependencyConfig, errorBuilder);
        this.mappers = SourceSetMapper.build(dependencyConfig, errorBuilder);

        if (dependencyConfig.getEnableIdeSupport() == null) {
            errorBuilder.append("DependencyConfig: 'enableIdeSupport' is null");
            this.ideSupport = false;
        } else this.ideSupport = dependencyConfig.getEnableIdeSupport();

        if (dependencyConfig.getRegisterDependencyRepositoryToProject() == null) {
            errorBuilder.append("DependencyConfig: 'registerDependencyRepositoryToProject' is null");
            this.shouldRegisterRepository = false;
        } else this.shouldRegisterRepository = dependencyConfig.getRegisterDependencyRepositoryToProject();

        if (dependencyConfig.getGenerateGradleTasks() == null) {
            errorBuilder.append("DependencyConfig: 'GenerateGradleTasks' is null");
            this.generateGradleTasks = false;
        } else this.generateGradleTasks = dependencyConfig.getGenerateGradleTasks();

        this.dependencyType = dependencyConfig.getDependencyType();
        if (dependencyType == null || dependencyConfig.getMavenDir() == null) {
            if (dependencyType == null) {
                errorBuilder.append("DependencyConfig: 'dependencyType' is null");
            } else {
                switch (dependencyType) {
                    case MavenProjectLocal:
                    case MavenProjectDependencyLocal:
                        errorBuilder.append("DependencyConfig: 'mavenDir' is null");
                }
            }

            this.mavenFolder = null;
        } else {
            switch (dependencyType) {
                case MavenProjectLocal:
                    this.mavenFolder = Constants.MavenProjectLocal.apply(dependencyConfig.getMavenDir());
                    break;

                case MavenProjectDependencyLocal:
                    this.mavenFolder = Constants.MavenProjectDependencyLocal.apply(dependencyConfig.getMavenDir(), name);
                    break;

                default:
                    this.mavenFolder = null;
            }
        }


        this.gitInfo = new GitInfo(managers, dependencyConfig, this, errorBuilder);
        this.gradleInfo = new GradleInfo(managers, dependencyConfig, this, errorBuilder);
        this.persistentInfo = new PersistentInfo(managers, dependencyConfig, this, errorBuilder);

        if (errorBuilder.hasErrors()) {
            throw new GradleException(errorBuilder.getMessage());
        }
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    @Unmodifiable
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @NotNull
    @Unmodifiable
    public List<SourceSetMapper> getSourceSetMappers() {
        return mappers;
    }

    public boolean isIdeSupport() {
        return ideSupport;
    }

    public boolean shouldRegisterRepository() {
        return shouldRegisterRepository;
    }

    public boolean isGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @NotNull
    public Type getDependencyType() {
        return dependencyType;
    }

    @Nullable
    public File getMavenFolder() {
        return mavenFolder;
    }

    @NotNull
    public GitInfo getGitInfo() {
        return gitInfo;
    }

    @NotNull
    public GradleInfo getGradleInfo() {
        return gradleInfo;
    }

    @NotNull
    public PersistentInfo getPersistentInfo() {
        return persistentInfo;
    }

    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    // TODO: 12/05/2023 depend on classes
    //Type of the crated dependency
    public enum Type {
        MavenLocal, //default maven local publishing
        MavenProjectLocal, //publishing to a maven inside the project file structure
        MavenProjectDependencyLocal, //same as MavenFileLocal except that every project has its own maven local folder
        JarFlatDir, //crates a flat dir repository at the build libs of the project
        Jar //directly add jar dependencies to the project
        // TODO: 18/02/2023 clean the build folder for the jar dependency, make it a task?
    }

}
