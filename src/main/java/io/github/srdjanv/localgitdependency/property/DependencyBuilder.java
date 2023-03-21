package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;

import java.util.List;

public interface DependencyBuilder {

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
     * This is used to filter out what jars will get added as dependencies.
     * This is only used for the Jar Dependency
     *
     * @param generatedJars Targeted jars
     * @see io.github.srdjanv.localgitdependency.depenency.Dependency.Type
     */
    void generatedJarsToAdd(List<String> generatedJars);

    /**
     * If the dependency is generating artifacts with a different name then the project id set the artifacts to be added.
     * If the supplied string contains ':' the plugin will assume that is a dependency notation(group:name:version).
     * Each element of the list will be added as a dependency
     *
     * @param generatedArtifactNames Targeted artifacts
     */
    void generatedArtifactNames(List<String> generatedArtifactNames);

    /**
     * Custom configuration for the dependency, this closure will be passed to the DependencyHandler
     *
     * @param configureClosure the closure to use to configure the dependency
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    void configure(@SuppressWarnings("rawtypes") Closure configureClosure);
}
