package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

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

    /**
     * @see io.github.srdjanv.localgitdependency.property.ArtifactBuilder
     */
    void configuration(@DelegatesTo(value = ArtifactBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure... configurations);

    /**
     * @see io.github.srdjanv.localgitdependency.property.SourceSetMapperBuilder
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
     * Some projects might require a one time configuration
     *
     * @param startupTasks task names
     */
    void oneTimeStartupTasks(String... startupTasks);
}
