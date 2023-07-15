package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface SubConfigurationBuilder {

    /**
     * The name of the recursive dependency, see the probe data for the exact name
     */
    void name(String name);

    /**
     * The default Configuration to add the generated artifacts
     * <p>
     * Preferably this should be a runtimeOnly configuration
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
}
