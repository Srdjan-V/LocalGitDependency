package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.ConfigurationBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ConfigurationConfig {
    private final String configuration;
    private final List<String> includeNotations;
    private final List<String> excludeNotations;
    private final Closure closure;
    private final Map<String, Closure> closureMap;

    public ConfigurationConfig(Builder builder) {
        this.configuration = builder.configuration;
        this.includeNotations = builder.includeNotations;
        this.excludeNotations = builder.excludeNotations;
        this.closure = builder.closure;
        this.closureMap = builder.closureMap;
    }

    @Nullable
    public String getConfiguration() {
        return configuration;
    }

    @Nullable
    public List<String> getIncludeNotations() {
        return includeNotations;
    }

    @Nullable
    public List<String> getExcludeNotations() {
        return excludeNotations;
    }

    @Nullable
    public Closure getClosure() {
        return closure;
    }

    @Nullable
    public Map<String, Closure> getClosureMap() {
        return closureMap;
    }

    public static class Builder implements ConfigurationBuilder {
        private String configuration;
        private final List<String> includeNotations;
        private final List<String> excludeNotations;
        private Closure closure;
        private Map<String, Closure> closureMap;

        public Builder() {
            includeNotations = new ArrayList<>();
            excludeNotations = new ArrayList<>();
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void closure(Closure closure) {
            this.closure = closure;
        }

        @Override
        public void include(String... notation) {
            includeNotations.addAll(Arrays.asList(notation));
        }

        @Override
        public void include(Map<String, Closure> notation) {
            closureMap = notation;
        }

        @Override
        public void exclude(String... notation) {
            excludeNotations.addAll(Arrays.asList(notation));
        }
    }
}
