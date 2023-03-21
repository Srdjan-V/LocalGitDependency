package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

public interface CommonBuilder {

    /**
     * To what configuration the dependency is going to be added
     *
     * @param configuration Configuration name
     * @see org.gradle.api.artifacts.ConfigurationContainer
     */
    void configuration(String configuration);

    /**
     * This will try to keep the gir repo of the dependency updated
     *
     * @param keepGitUpdated True if the plugin should update the git repo if the target commit changes
     */
    void keepGitUpdated(Boolean keepGitUpdated);

    /**
     * If set to false the generated dependencyInitScript will never be updated of fixed if changes are detected
     *
     * @param keepDependencyInitScriptUpdated If it should stay updated
     */
    void keepDependencyInitScriptUpdated(Boolean keepDependencyInitScriptUpdated);

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param dir directory target
     */
    void gitDir(File dir);

    /**
     * Same as the above, but takes a string
     *
     * @param dir directory target
     */
    void gitDir(String dir);

    /**
     * Some project need a specific java version to be build, specify the path of the JDK
     *
     * @param javaHomeDir Path to the JDK
     */
    void javaHomeDir(File javaHomeDir);

    /**
     * Same as the above, but takes a string
     *
     * @param javaHomeDir directory target
     */
    void javaHomeDir(String javaHomeDir);

    /**
     * This will set the directory in which the dependency data will be stored, it will not set the folder.
     * If you set the path to "./folder" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param persistentDir directory target
     */
    void persistentDir(File persistentDir);

    /**
     * Same as the above, but takes a string
     *
     * @param persistentDir directory target
     */
    void persistentDir(String persistentDir);


    /**
     * Some Dependency types will publish the artifact to local maven repo.
     *
     * @param mavenDir directory target
     * @see Dependency.Type
     */
    void mavenDir(File mavenDir);

    /**
     * Same as the above, but takes a string
     *
     * @param mavenDir directory target
     */
    void mavenDir(String mavenDir);

    /**
     * How the dependency is going to be added to the project
     *
     * @param dependencyType dependency target
     * @see Dependency.Type
     */
    void dependencyType(Dependency.Type dependencyType);

    /**
     * If the cloned dependency doesn't have a task that is going to make a source jar, the plugin can try to generate one
     *
     * @param tryGeneratingSourceJar if it should try
     */
    void tryGeneratingSourceJar(Boolean tryGeneratingSourceJar);

    /**
     * If the cloned dependency doesn't have a task that is going to make a java doc jar, the plugin can try to generate one
     *
     * @param tryGeneratingJavaDocJar if it should try
     */
    void tryGeneratingJavaDocJar(Boolean tryGeneratingJavaDocJar);

    /**
     * If you plan on editing this dependency from the same ide enable this.
     * By enabling this the plugin will register the source sets, configurations, repositories and its dependencies to your project.
     * By default, its disabled
     *
     * @param enableIdeSupport if it should enable ide support
     */
    void enableIdeSupport(Boolean enableIdeSupport);

    /**
     * If the built dependencies should be added as dependencies.
     * You can manually add the dependency manually
     *
     * @param registerDependencyToProject if it should register the dependency
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    void registerDependencyToProject(Boolean registerDependencyToProject);

    /**
     * If a repository should be added for the build dependency, this is will not do anything for the Jar dependency type
     *
     * @param registerDependencyRepositoryToProject if it should register the dependency
     * @see Dependency.Type
     * @see org.gradle.api.artifacts.dsl.RepositoryHandler
     */
    void registerDependencyRepositoryToProject(Boolean registerDependencyRepositoryToProject);

    /**
     * Custom tasks can be generated for this dependency
     *
     * @param generateGradleTasks if it should create custom tasks
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    void generateGradleTasks(Boolean generateGradleTasks);

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @param gradleDaemonMaxIdleTime the amount of time in seconds
     * @see java.util.concurrent.TimeUnit
     */
    void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime);

}
