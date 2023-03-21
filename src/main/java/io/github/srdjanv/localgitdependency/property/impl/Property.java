package io.github.srdjanv.localgitdependency.property.impl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.property.DependencyBuilder;
import io.github.srdjanv.localgitdependency.property.PropertyManager;

import java.util.List;

/**
 * Property's that only a dependency can have
 */
public class Property extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String target;
    private final GitInfo.TargetType targetType;
    private final List<String> generatedJarsToAdd;
    private final List<String> generatedArtifactNames;
    private final Closure<?> configureClosure;

    public Property(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        generatedJarsToAdd = builder.generatedJarsToAdd;
        generatedArtifactNames = builder.generatedArtifactNames;
        configureClosure = builder.configureClosure;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public GitInfo.TargetType getTargetType() {
        return targetType;
    }

    public List<String> getGeneratedJarsToAdd() {
        return generatedJarsToAdd;
    }

    public List<String> getGeneratedArtifactNames() {
        return generatedArtifactNames;
    }

    public Closure<?> getConfigureClosure() {
        return configureClosure;
    }

    public static class Builder extends CommonPropertyBuilder implements DependencyBuilder {
        private final String url;
        private String name;
        private String target;
        private GitInfo.TargetType targetType;
        private List<String> generatedJarsToAdd;
        private List<String> generatedArtifactNames;
        private Closure<?> configureClosure;

        public Builder(String url) {
            this.url = url;
        }

        /**
         * Sets the name of the dependency, it will also be used as the directory name
         *
         * @param name Dependency name
         */
        public void name(String name) {
            this.name = name;
        }

        public void commit(String commit) {
            targetType = GitInfo.TargetType.COMMIT;
            this.target = commit;
        }

        public void branch(String branch) {
            targetType = GitInfo.TargetType.BRANCH;
            this.target = branch;
        }

        public void tag(String tag) {
            targetType = GitInfo.TargetType.TAG;
            this.target = tag;
        }

        /**
         * This is used to filter out what jars will get added as dependencies.
         * This is only used for the Jar Dependency
         *
         * @param generatedJars Targeted jars
         * @see io.github.srdjanv.localgitdependency.depenency.Dependency.Type
         */
        public void generatedJarsToAdd(List<String> generatedJars) {
            this.generatedJarsToAdd = generatedJars;
        }

        /**
         * If the dependency is generating artifacts with a different name then the project id set the artifacts to be added.
         * If the supplied string contains ':' the plugin will assume that is a dependency notation(group:name:version).
         * Each element of the list will be added as a dependency
         * @param generatedArtifactNames Targeted artifacts
         */
        public void generatedArtifactNames(List<String> generatedArtifactNames) {
            this.generatedArtifactNames = generatedArtifactNames;
        }

        /**
         * Custom configuration for the dependency, this closure will be passed to the DependencyHandler
         *
         * @param configureClosure the closure to use to configure the dependency
         * @see org.gradle.api.artifacts.dsl.DependencyHandler
         */
        public void configure(@SuppressWarnings("rawtypes") Closure configureClosure) {
            this.configureClosure = configureClosure;
        }

    }
}
