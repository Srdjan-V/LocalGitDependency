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
     * @param initScript directory target
     */
    public void persistentFolder(File initScript) {
        this.persistentFolder = initScript;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param initScript directory target
     */
    public void persistentFolder(String initScript) {
        this.persistentFolder = new File(initScript);
    }


    /**
     * Some Dependency types will publish the artifact to local maven repo.
     *
     * @param mavenFolder directory target
     * @see Dependency.Type
     */
    public void mavenFolder(File mavenFolder) {
        this.mavenFolder = mavenFolder;
    }

    /**
     * Same as the above, but takes a string
     *
     * @param mavenFolder directory target
     */
    public void mavenFolder(String mavenFolder) {
        this.mavenFolder = new File(mavenFolder);
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
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     * @param registerDependencyToProject if it should register the dependency
     */
    public void registerDependencyToProject(Boolean registerDependencyToProject) {
        this.registerDependencyToProject = registerDependencyToProject;
    }

    /**
     * Custom tasks can be generated for this dependency
     *
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     * @param generateGradleTasks if it should create custom tasks
     */
    public void generateGradleTasks(Boolean generateGradleTasks) {
        this.generateGradleTasks = generateGradleTasks;
    }

}
