package io.github.srdjanv.localgitdependency.property.impl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.property.DependencyBuilder;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

import java.util.List;

/**
 * Property's that only a dependency can have
 */
public class DependencyProperty extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String target;
    private final GitInfo.TargetType targetType;
    private final List<String> generatedJarsToAdd;
    private final List<String> generatedArtifactNames;
    private final Closure<?> configureClosure;

    public DependencyProperty(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        generatedJarsToAdd = builder.generatedJarsToAdd;
        generatedArtifactNames = builder.generatedArtifactNames;
        configureClosure = builder.configureClosure;
        BuilderUtil.instantiateObjectWithBuilder(this, builder, CommonPropertyFields.class);
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

        public void generatedJarsToAdd(List<String> generatedJars) {
            this.generatedJarsToAdd = generatedJars;
        }

        public void generatedArtifactNames(List<String> generatedArtifactNames) {
            this.generatedArtifactNames = generatedArtifactNames;
        }

        public void configure(@SuppressWarnings("rawtypes") Closure configureClosure) {
            this.configureClosure = configureClosure;
        }

    }
}
