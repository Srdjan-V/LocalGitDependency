package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.property.Property;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dependency {
    private final String name;
    private final String configurationName;
    private final List<String> generatedJarsToAdd;
    private final List<String> generatedArtifactNames;
    private final boolean addDependencySourcesToProject;
    private final boolean registerDependencyToProject;
    private final boolean generateGradleTasks;
    private final Closure<?> configureClosure;// TODO: 12/03/2023 discard object after dependency registration
    private final Type dependencyType;
    private final File mavenFolder;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;
    private final PersistentInfo persistentInfo;

    public Dependency(String configurationName, Property dependencyProperty) {
        Instances.getPropertyManager().applyDefaultProperty(dependencyProperty);

        this.name = dependencyProperty.getName() == null ? getNameFromUrl(dependencyProperty.getUrl()) : dependencyProperty.getName();
        this.configurationName = configurationName == null ? dependencyProperty.getConfiguration() : configurationName;
        this.generatedJarsToAdd = dependencyProperty.getGeneratedJarsToAdd();
        this.generatedArtifactNames = dependencyProperty.getGeneratedArtifactNames();
        this.addDependencySourcesToProject = dependencyProperty.getAddDependencySourcesToProject();
        this.registerDependencyToProject = dependencyProperty.getRegisterDependencyToProject();
        this.generateGradleTasks = dependencyProperty.getGenerateGradleTasks();
        this.configureClosure = dependencyProperty.getConfigureClosure();
        this.dependencyType = dependencyProperty.getDependencyType();
        switch (dependencyType) {
            case MavenProjectLocal:
                this.mavenFolder = Constants.MavenProjectLocal.apply(dependencyProperty.getMavenFolder());
                break;

            case MavenProjectDependencyLocal:
                this.mavenFolder = Constants.MavenProjectDependencyLocal.apply(dependencyProperty.getMavenFolder(), name);
                break;

            default:
                this.mavenFolder = null;
        }

        this.gitInfo = new GitInfo(dependencyProperty, this);
        this.gradleInfo = new GradleInfo(dependencyProperty, this);
        this.persistentInfo = new PersistentInfo(dependencyProperty, this);
        validate();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getConfigurationName() {
        return configurationName;
    }

    @Nullable
    public List<String> getGeneratedJarsToAdd() {
        return generatedJarsToAdd;
    }

    @Nullable
    public List<String> getGeneratedArtifactNames() {
        return generatedArtifactNames;
    }

    public boolean isAddDependencySourcesToProject() {
        return addDependencySourcesToProject;
    }

    public boolean isRegisterDependencyToProject() {
        return registerDependencyToProject;
    }

    public boolean isGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @Nullable
    public Closure<?> getConfigureClosure() {
        return configureClosure;
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

    // TODO: 18/02/2023 add all of the parameters for validation
    private void validate() {
        StringBuilder errors = null;

        if (gitInfo.getUrl() == null) {
            errors = crateStringBuilder(null);
            errors.append("Property: 'url' is not specified").append(System.lineSeparator());
        }
        if (name == null) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'name' is not specified").append(System.lineSeparator());
        }
        if (gitInfo.getDir().exists() && !gitInfo.getDir().isDirectory()) {
            errors = crateStringBuilder(errors);
            errors.append("Property: 'dir' is not a directory ").append(gitInfo.getDir()).append(System.lineSeparator());
        }

        if (errors != null) {
            throw new GradleException(errors.toString());
        }
    }

    private static String getNameFromUrl(String url) {
        if (url == null) return null;
        // Splitting last url's part before ".git" suffix
        Matcher matcher = Pattern.compile("([^/]+)\\.git$").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private StringBuilder crateStringBuilder(StringBuilder errors) {
        if (errors == null) {
            return new StringBuilder("Git dependency errors:").append(System.lineSeparator());
        }
        return errors;
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
