package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

/**
 * Base property's used for dependency and global configuration.
 * A new dependency will inherit properties from the global configuration.
 */
@SuppressWarnings("unused")
public abstract class CommonPropertyBuilder extends CommonPropertyFields {
    CommonPropertyBuilder() {
    }

    /**
     * To what configuration the dependency is going to be added
     *
     * @param configuration Configuration name
     * @see org.gradle.api.artifacts.ConfigurationContainer
     */
    public void configuration(String configuration) {
        this.configuration = configuration;
    }

    /**
     * This will try to keep the gir repo of the dependency updated
     *
     * @param keepGitUpdated True if the plugin should update the git repo if the target commit changes
     */
    public void keepGitUpdated(Boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }

    /**
     * If set to false the generated dependencyInitScript will never be updated of fixed if changes are detected
     *
     * @param keepDependencyInitScriptUpdated If it should stay updated
     */
    public void keepDependencyInitScriptUpdated(Boolean keepDependencyInitScriptUpdated) {
        this.keepDependencyInitScriptUpdated = keepDependencyInitScriptUpdated;
    }

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param dir directory target
     */
    public void gitDir(File dir) {
        this.gitDir = dir;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param dir directory target
     */
    public void gitDir(String dir) {
        this.gitDir = new File(dir);
    }

    /**
     * Some project need a specific java version to be build, specify the path of the JDK
     *
     * @param javaHomeDir Path to the JDK
     */
    public void javaHomeDir(File javaHomeDir) {
        this.javaHomeDir = javaHomeDir;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param javaHomeDir directory target
     */
    public void javaHomeDir(String javaHomeDir) {
        this.javaHomeDir = new File(javaHomeDir);
    }

    /**
     * This will set the directory in which the dependency data will be stored, it will not set the folder.
     * If you set the path to "./folder" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param persistentDir directory target
     */
    public void persistentDir(File persistentDir) {
        this.persistentDir = persistentDir;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param persistentDir directory target
     */
    public void persistentDir(String persistentDir) {
        this.persistentDir = new File(persistentDir);
    }


    /**
     * Some Dependency types will publish the artifact to local maven repo.
     *
     * @param mavenDir directory target
     * @see Dependency.Type
     */
    public void mavenDir(File mavenDir) {
        this.mavenDir = mavenDir;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param mavenDir directory target
     */
    public void mavenDir(String mavenDir) {
        this.mavenDir = new File(mavenDir);
    }

    /**
     * How the dependency is going to be added to the project
     *
     * @param dependencyType dependency target
     * @see Dependency.Type
     */
    public void dependencyType(Dependency.Type dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * If the cloned dependency doesn't have a task that is going to make a source jar, the plugin can try to generate one
     *
     * @param tryGeneratingSourceJar if it should try
     */
    public void tryGeneratingSourceJar(Boolean tryGeneratingSourceJar) {
        this.tryGeneratingSourceJar = tryGeneratingSourceJar;
    }

    /**
     * If the cloned dependency doesn't have a task that is going to make a java doc jar, the plugin can try to generate one
     *
     * @param tryGeneratingJavaDocJar if it should try
     */
    public void tryGeneratingJavaDocJar(Boolean tryGeneratingJavaDocJar) {
        this.tryGeneratingJavaDocJar = tryGeneratingJavaDocJar;
    }

    /**
     * If the cloned dependencies source sets should be added to the main project.
     *
     * @param addDependencySourcesToProject if it should add the source sets
     */
    public void addDependencySourcesToProject(Boolean addDependencySourcesToProject) {
        this.addDependencySourcesToProject = addDependencySourcesToProject;
    }

    /**
     * If the built dependencies should be added as dependencies.
     * You can manually add the dependency manually
     *
     * @param registerDependencyToProject if it should register the dependency
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    public void registerDependencyToProject(Boolean registerDependencyToProject) {
        this.registerDependencyToProject = registerDependencyToProject;
    }

    /**
     * If a repository should be added for the build dependency, this is will not do anything for the Jar dependency type
     *
     * @param registerDependencyRepositoryToProject if it should register the dependency
     * @see Dependency.Type
     * @see org.gradle.api.artifacts.dsl.RepositoryHandler
     */
    public void registerDependencyRepositoryToProject(Boolean registerDependencyRepositoryToProject) {
        this.registerDependencyRepositoryToProject = registerDependencyRepositoryToProject;
    }


    /**
     * Custom tasks can be generated for this dependency
     *
     * @param generateGradleTasks if it should create custom tasks
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    public void generateGradleTasks(Boolean generateGradleTasks) {
        this.generateGradleTasks = generateGradleTasks;
    }

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @param gradleDaemonMaxIdleTime the amount of time in seconds
     * @see java.util.concurrent.TimeUnit
     */
    public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
        this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
    }

}
