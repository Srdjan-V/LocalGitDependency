package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.property.SourceSetMapperBuilder;

public class SourceSetMapper {
    private final String projectSet;
    private final String[] dependencySet;

    public SourceSetMapper(Builder builder) {
        this.projectSet = builder.projectSet;
        this.dependencySet = builder.dependencySet;
    }

    public String getProjectSet() {
        return projectSet;
    }

    public String[] getDependencySet() {
        return dependencySet;
    }

    public static class Builder implements SourceSetMapperBuilder {
        private String projectSet;
        private String[] dependencySet;

        @Override
        public void map(String projectSet, String... dependencySet) {
            this.projectSet = projectSet;
            this.dependencySet = dependencySet;
        }
    }
}
