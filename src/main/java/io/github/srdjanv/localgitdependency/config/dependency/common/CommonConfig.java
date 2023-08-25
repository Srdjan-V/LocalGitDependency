package io.github.srdjanv.localgitdependency.config.dependency.common;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.provider.Property;

public interface CommonConfig {

    /**
     * This will try to keep the gir repo of the dependency updated
     *
     * @param keepGitUpdated True if the plugin should update the git repo if the target commit changes
     */
    Property<Boolean> keepGitUpdated();

    /**
     * If set to false the generated dependencyInitScript will never be updated of fixed if changes are detected
     *
     * @param keepDependencyInitScriptUpdated If it should stay updated
     */
    Property<Boolean> keepInitScriptUpdated();

    /**
     * If the cloned dependency doesn't have a task that is going to make a source jar, the plugin can try to generate one
     *
     * @param tryGeneratingSourceJar if it should try
     */
    Property<Boolean> tryGeneratingSourceJar();

    /**
     * If the cloned dependency doesn't have a task that is going to make a java doc jar, the plugin can try to generate one
     *
     * @param tryGeneratingJavaDocJar if it should try
     */
    Property<Boolean> tryGeneratingJavaDocJar();

    /**
     * If a repository should be added for the build dependency, this will not do anything for the Jar dependency type
     *
     * @param registerDependencyRepositoryToProject if it should register the dependency
     * @see Dependency.Type
     * @see org.gradle.api.artifacts.dsl.RepositoryHandler
     */
    Property<Boolean> registerDependencyRepositoryToProject();

    /**
     * Custom tasks can be generated for this dependency
     *
     * @param generateGradleTasks if it should create custom tasks
     */
    Property<Boolean> generateGradleTasks();
}
