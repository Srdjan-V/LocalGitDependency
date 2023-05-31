package io.github.srdjanv.localgitdependency.config.impl.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapperBuilder;
import org.jetbrains.annotations.Nullable;

public final class SourceSetMapperConfig {
    private final String projectSet;
    private final String[] dependencySet;
    private final Boolean recursive;

    public SourceSetMapperConfig(Builder builder) {
        this.projectSet = builder.projectSet;
        this.dependencySet = builder.dependencySet;
        this.recursive = builder.recursive;
    }

    @Nullable
    public String getProjectSet() {
        return projectSet;
    }

    @Nullable
    public String[] getDependencySet() {
        return dependencySet;
    }

    @Nullable
    public Boolean isRecursive() {
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
