package io.github.srdjanv.localgitdependency.property.impl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.property.DependencyBuilder;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

import java.util.List;
import java.util.Map;

/**
 * Property's that only a dependency can have
 */
public class DependencyProperty extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String target;
    private final GitInfo.TargetType targetType;
    private final String configuration;
    private final Map<String, List<Closure>> configurations;
    public DependencyProperty(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        configuration = builder.configuration;
        configurations = builder.configurations;
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

    public String getConfiguration() {
        return configuration;
    }

    public Map<String, List<Closure>> getConfigurations() {
        return configurations;
    }

    public static class Builder extends CommonPropertyBuilder implements DependencyBuilder {
        private final String url;
        private String name;
        private String target;
        private GitInfo.TargetType targetType;
        private String configuration;
        private Map<String, List<Closure>> configurations;

        public Builder(String url) {
            this.url = url;
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void configuration(Map<String, List<Closure>> configurations) {
            this.configurations = configurations;
        }

        @Override
        public void name(String name) {
            this.name = name;
        }

        @Override
        public void commit(String commit) {
            targetType = GitInfo.TargetType.COMMIT;
            this.target = commit;
        }

        @Override
        public void branch(String branch) {
            targetType = GitInfo.TargetType.BRANCH;
            this.target = branch;
        }

        @Override
        public void tag(String tag) {
            targetType = GitInfo.TargetType.TAG;
            this.target = tag;
        }

    }
}
