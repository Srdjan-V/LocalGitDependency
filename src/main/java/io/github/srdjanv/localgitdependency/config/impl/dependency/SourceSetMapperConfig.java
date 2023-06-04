package io.github.srdjanv.localgitdependency.config.impl.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapperBuilder;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.jetbrains.annotations.NotNull;

@NonNullData
public final class SourceSetMapperConfig {
    private final String projectSet;
    private final String[] dependencySet;
    private final Boolean recursive;

    public SourceSetMapperConfig(Builder builder) {
        this.projectSet = builder.projectSet;
        this.dependencySet = builder.dependencySet;
        this.recursive = builder.recursive;
    }

    @NotNull
    public String getProjectSet() {
        return projectSet;
    }

    @NotNull
    public String[] getDependencySet() {
        return dependencySet;
    }

    @NotNull
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
