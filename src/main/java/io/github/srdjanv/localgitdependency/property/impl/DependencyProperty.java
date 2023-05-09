package io.github.srdjanv.localgitdependency.property.impl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.property.DependencyBuilder;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

/**
 * Property's that only a dependency can have
 */
public class DependencyProperty extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String target;
    private final GitInfo.TargetType targetType;
    private final String configuration;
    private final Closure[] configurations;
    private final Closure[] mappings;
    private final String[] startupTasks;

    public DependencyProperty(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        configuration = builder.configuration;
        configurations = builder.configurations;
        mappings = builder.mappings;
        startupTasks = builder.startupTasks;
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

    public Closure[] getConfigurations() {
        return configurations;
    }

    public Closure[] getMappings() {
        return mappings;
    }

    public String[] getStartupTasks() {
        return startupTasks;
    }

    public static class Builder extends CommonPropertyBuilder implements DependencyBuilder {
        private final String url;
        private String name;
        private String target;
        private GitInfo.TargetType targetType;
        private String configuration;
        private Closure[] configurations;
        private Closure[] mappings;
        private String[] startupTasks;

        public Builder(String url) {
            this.url = url;
        }

        @Override
        public void configuration(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public void configuration(Closure... configurations) {
            this.configurations = configurations;
        }

        @Override
        public void mapSourceSets(Closure... mappings) {
            this.mappings = mappings;
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

        @Override
        public void oneTimeStartupTasks(String... startupTasks) {
            this.startupTasks = startupTasks;
        }
    }
}
