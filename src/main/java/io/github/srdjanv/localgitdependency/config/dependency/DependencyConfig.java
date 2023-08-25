package io.github.srdjanv.localgitdependency.config.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.common.CommonConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.provider.Property;

public interface DependencyConfig extends CommonConfig {
    Property<String> url();

    /**
     * Sets the name of the dependency, it will also be used as the directory name
     *
     * @param name Dependency name
     */
    Property<String> name();

    Property<String> commit();

    Property<String> branch();

    Property<String> tag();

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param dir directory target
     */
    Property<Object> gitDir();

    /**
     * This will set the directory in which the dependency data will be stored, it will not set the folder.
     * If you set the path to "./folder" the dependency will make a directory based of the dependency name inside the folder
     *
     * @param persistentDir directory target
     */
    Property<Object> persistentDir();

    /**
     * Some Dependency types will publish the artifact to local maven repo.
     *
     * @param mavenDir directory target
     * @see Dependency.Type
     */
    Property<Object> mavenDir();

    /**
     * @see LauncherConfig
     */
    Property<LauncherConfig> buildLauncher();

}
