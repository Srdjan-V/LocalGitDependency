package io.github.srdjanv.localgitdependency.property.impl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.property.ArtifactBuilder;
import org.gradle.internal.impldep.com.sun.istack.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class Artifact {
    private final String configuration;
    private final Closure closure;
    private final List<Property> artifactProperty;

    public Artifact(Builder builder) {
        this.configuration = builder.configuration;
        this.closure = builder.closure;
        this.artifactProperty = new ArrayList<>();

        Set<Property> properties = new HashSet<>();

        for (String includeNotation : builder.includeNotations) {
            Property prop;
            var closure = builder.closureMap.get(includeNotation);
            if (closure == null) {
                prop = new Property(includeNotation, this.closure);
            } else {
                prop = new Property(includeNotation, closure);
            }
            properties.add(prop);
        }

        for (String includeNotation : builder.excludeNotations) {
            Property prop = new Property(includeNotation);
            if (!properties.add(prop)) {
                properties.remove(prop);
                properties.add(prop);
            }
        }
        artifactProperty.addAll(properties);
    }

    @NotNull
    public String getConfiguration() {
        return configuration;
    }

    public Closure getClosure() {
        return closure;
    }

    @NotNull
    @Unmodifiable
    public List<Property> getArtifactProperty() {
        return Collections.unmodifiableList(artifactProperty);
    }

    public static class Property {
        private final String notation;
        private final boolean include;
        private final Closure closure;

        private Property(String notation, Closure closure) {
            this.notation = notation;
            this.closure = closure;
            this.include = true;
        }

        private Property(String notation) {
            this.notation = notation;
            this.closure = null;
            this.include = false;
        }

        public String notation() {
            return notation;
        }

        public boolean include() {
            return include;
        }

        public Closure closure() {
            return closure;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Property property = (Property) o;
            return notation.equals(property.notation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(notation);
        }
    }

    public static class Builder implements ArtifactBuilder {
        private String configuration;
        private final List<String> includeNotations;
        private final List<String> excludeNotations;
        private Closure closure;
        private final Map<String, Closure> closureMap;

        public Builder() {
            includeNotations = new ArrayList<>();
            excludeNotations = new ArrayList<>();
            closureMap = new HashMap<>();
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void include(String... notation) {
            includeNotations.addAll(Arrays.asList(notation));
        }

        @Override
        public void exclude(String... notation) {
            excludeNotations.addAll(Arrays.asList(notation));
        }

        @Override
        public void closure(Closure closure) {
            this.closure = closure;
        }

        @Override
        public void closure(String notation, Closure closure) {
            closureMap.put(notation, closure);
        }
    }
}
