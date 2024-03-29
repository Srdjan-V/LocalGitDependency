package io.github.srdjanv.localgitdependency.config.plugin;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface PluginBuilder {

    /**
     * This will set the directory for all the default folder.
     * The default is localGitDependency
     * @param dir directory target
     */
    void defaultDir(Object dir);

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param gitDir directory target
     */
    void gitDir(Object gitDir);

    /**
     * This will set the directory in which the dependency data will be stored, it will not set the folder.
     * If you set the path to "./folder" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param persistentDir directory target
     */
    void persistentDir(Object persistentDir);

    /**
     * Some Dependency types will publish the artifact to local maven repo.
     *
     * @param mavenDir directory target
     * @see Dependency.Type
     */
    void mavenDir(Object mavenDir);

    /**
     * If set to false the generated mainInitScript will never be updated of fixed if changes are detected
     *
     * @param keepInitScriptUpdated If it should stay updated
     */
    void keepInitScriptUpdated(Boolean keepInitScriptUpdated);

    /**
     * This will generate default tasks
     *
     * @param generateGradleTasks if it should create custom tasks
     */
    void generateGradleTasks(Boolean generateGradleTasks);

    /**
     * Cleanup removed dependencies. It's enabled by default, but if you specify a custom global path you must explicitly enable it.
     * This is done because the cleanupManager will delete everything under those directories that doesn't mach with a registered dependency
     *
     * @param automaticCleanup if it should cleanup removed dependencies
     */
    void automaticCleanup(Boolean automaticCleanup);
}
