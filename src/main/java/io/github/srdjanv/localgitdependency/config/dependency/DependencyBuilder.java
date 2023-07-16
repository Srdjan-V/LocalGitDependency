package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface DependencyBuilder extends DefaultableBuilder {

    /**
     * @see LauncherBuilder
     */
    void buildLauncher(@DelegatesTo(value = LauncherBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure launcher);

    /**
     * To what configuration to add all the generated jars
     * <p>
     * preferably this should be a runtimeOnly configuration
     *
     * @param configuration Configuration name
     * @see org.gradle.api.artifacts.ConfigurationContainer
     */
    void configuration(String configuration);

    /**
     * @see ConfigurationBuilder
     */
    void configuration(@DelegatesTo(value = ConfigurationBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure... configurations);

    /**
     * @see ConfigurationBuilder
     */
    void subConfiguration(@DelegatesTo(value = SubConfigurationBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure... configurations);

    /**
     * @see SourceSetMapperBuilder
     */
    void mapSourceSets(@DelegatesTo(value = SourceSetMapperBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure... mappings);

    /**
     * Sets the name of the dependency, it will also be used as the directory name
     *
     * @param name Dependency name
     */
    void name(String name);

    void commit(String commit);

    void branch(String branch);

    void tag(String tag);

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param dir directory target
     */
    void gitDir(Object dir);

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
}
