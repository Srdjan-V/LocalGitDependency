package io.github.srdjanv.localgitdependency.config.dependency.common;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface CommonConfig {

    /**
     * This will try to keep the gir repo of the dependency updated
     *
     */
    Property<Boolean> getKeepGitUpdated();

    /**
     * If set to false the generated dependencyInitScript will never be updated of fixed if changes are detected
     *
     */
    Property<Boolean> getKeepInitScriptUpdated();

    /**
     * If the cloned dependency doesn't have a task that is going to make a source jar, the plugin can try to generate one
     *
     */
    Property<Boolean> getTryGeneratingSourceJar();

    /**
     * If the cloned dependency doesn't have a task that is going to make a java doc jar, the plugin can try to generate one
     *
     */
    Property<Boolean> getTryGeneratingJavaDocJar();

    /**
     * If a repository should be added for the build dependency, this will not do anything for the Jar dependency type
     *
     * @see Dependency.Type
     * @see org.gradle.api.artifacts.dsl.RepositoryHandler
     */
    Property<Boolean> getRegisterDependencyRepositoryToProject();

    /**
     * Custom tasks can be generated for this dependency
     *
     */
    Property<Boolean> getGenerateGradleTasks();

    SetProperty<Dependency.Type> getBuildTargets();
}
