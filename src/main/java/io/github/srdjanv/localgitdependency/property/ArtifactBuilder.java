package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;

public interface ArtifactBuilder {
    void configuration(String configuration);
    void include(String... notation);
    void exclude(String... notation);
    void closure(Closure closure);
    void closure(String notation, Closure closure);
}
