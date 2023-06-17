package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface SubConfigurationBuilder {

    /**
     * The name of the dependency
     * @see ConfigurationBuilder
     */
    void name(String name);

    void configuration(String configuration);

    /**
     * @see ConfigurationBuilder
     */
    void configuration(@DelegatesTo(value = ConfigurationBuilder.class,
            strategy = Closure.DELEGATE_FIRST) Closure... configurations);
}
