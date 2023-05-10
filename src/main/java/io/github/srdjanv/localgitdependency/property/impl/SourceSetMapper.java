package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.property.SourceSetMapperBuilder;

public class SourceSetMapper {
    private final String projectSet;
    private final String[] dependencySet;
    private final boolean recursive;

    public SourceSetMapper(Builder builder) {
        this.projectSet = builder.projectSet;
        this.dependencySet = builder.dependencySet;
        this.recursive = builder.recursive != null ? builder.recursive : true;
    }

    public String getProjectSet() {
        return projectSet;
    }

    public String[] getDependencySet() {
        return dependencySet;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public static class Builder implements SourceSetMapperBuilder {
        private String projectSet;
        private String[] dependencySet;
        private Boolean recursive;

        @Override
        public void map(String projectSet, String... dependencySet) {
            this.projectSet = projectSet;
            this.dependencySet = dependencySet;
        }

        @Override
        public void recursive(Boolean recursive) {
            this.recursive = recursive;
        }
    }
}
