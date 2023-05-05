package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

public interface DependencyBuilder extends CommonBuilder {

    /**
     * To what configuration to add all the generated jars
     * <p>
     * preferably this should be a runtimeOnly configuration
     *
     * @param configuration Configuration name
     * @see org.gradle.api.artifacts.ConfigurationContainer
     */
    void configuration(String configuration);

    // TODO: 05/05/2023 fix doc 
    /**
     * The map key represents the configuration and the list represents the generated artifacts
     * <p>
     * If the dependency type is Jar then the map will use string contains to filter what jars are going to be added
     * <p>
     * If its any other type the string will act as a dependency notation, you can add only the jar name and the program
     * will add the group and version automatically. If the supplied string contains ':' the plugin will assume that is
     * a full dependency notation(group:name:version).
     * * <p>
     * If the key is a empty list all artifacts are going to be added
     * <p>
     * preferably this should be a runtimeOnly configuration
     *
     * @param configurations Configuration name jar mapper
     * @see org.gradle.api.artifacts.ConfigurationContainer
     * @see io.github.srdjanv.localgitdependency.depenency.Dependency.Type
     */

    void configuration(Map<String, List<Closure>> configurations);

    /**
     * Sets the name of the dependency, it will also be used as the directory name
     *
     * @param name Dependency name
     */
    void name(String name);

    void commit(String commit);

    void branch(String branch);

    void tag(String tag);

}
