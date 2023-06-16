package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;

import java.util.Map;

public interface ConfigurationBuilder {
    /**
     * The target configuration for artifacts
     * <p>
     * preferably this should be a runtimeOnly configuration
     *
     * @param configuration Configuration name
     * @see org.gradle.api.artifacts.ConfigurationContainer
     */
    void configuration(String configuration);

    /**
     * This closure will be passed to the DependencyHandler
     * <p>
     *  If left empty the closure  of the dependency builder will be used
     *
     * @param closure artifact closure
     * @see org.gradle.api.artifacts.dsl.DependencyHandler
     */
    void closure(Closure closure);

    /**
     * If the dependency type is Jar then the plugin will use string contains to filter what jars are going to be added
     * <p>
     * If its any other type the string will act as a dependency notation, you can add only the artifact name and the plugin
     * will add the group and version automatically. If the supplied string contains ':' the plugin will assume that is
     * a full dependency notation(group:name:version).
     * * <p>
     *
     * @param notation include notations
     * @see org.gradle.api.artifacts.ConfigurationContainer
     * @see io.github.srdjanv.localgitdependency.depenency.Dependency.Type
     */
    void include(String... notation);

    void include(Map<String, Closure> notation);

    /**
     * Same as the above but is used for exclusion, this should not be used alongside include
     *
     * @param notation include notations
     * @see org.gradle.api.artifacts.ConfigurationContainer
     * @see io.github.srdjanv.localgitdependency.depenency.Dependency.Type
     */
    void exclude(String... notation);

}
