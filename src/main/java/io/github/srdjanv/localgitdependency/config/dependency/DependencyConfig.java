package io.github.srdjanv.localgitdependency.config.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.common.CommonConfig;
import org.gradle.api.provider.Property;

public interface DependencyConfig extends CommonConfig {
    Property<String> getUrl();

    /**
     * Sets the name of the dependency, it will also be used as the directory name
     */
    Property<String> getName();

    Property<String> getCommit();

    Property<String> getBranch();

    Property<String> getTag();

    /**
     * This will set the directory in which the dependency will be created, it will not set the folder.
     * If you set the path to "./libs" the dependency will make a directory based of the dependency name inside the folder
     *
     */
    Property<Object> getLibsDir();

    /**
     * @see LauncherConfig
     */
    LauncherConfig getBuildLauncher();

}
