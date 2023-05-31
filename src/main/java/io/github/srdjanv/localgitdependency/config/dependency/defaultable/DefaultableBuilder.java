package io.github.srdjanv.localgitdependency.config.dependency.defaultable;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface DefaultableBuilder {

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
    void keepInitScriptUpdated(Boolean keepDependencyInitScriptUpdated);

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
     * By enabling this the plugin will register the source sets, configurations, its dependencies to your project.
     * <p>
     * Disabled by default
     *
     * @param enableIdeSupport if it should enable ide support
     */
    void enableIdeSupport(Boolean enableIdeSupport);

    /**
     * If a repository should be added for the build dependency, this will not do anything for the Jar dependency type
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
