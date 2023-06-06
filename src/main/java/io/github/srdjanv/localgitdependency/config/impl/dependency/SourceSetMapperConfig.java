package io.github.srdjanv.localgitdependency.config.impl.dependency;

import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapperBuilder;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.jetbrains.annotations.NotNull;

@NonNullData
public final class SourceSetMapperConfig {
    private final String projectSet;
    private final String[] dependencySet;
    private final boolean recursive;

    public SourceSetMapperConfig(Builder builder) {
        this.projectSet = builder.projectSet;
        this.dependencySet = builder.dependencySet;
        if (builder.recursive != null) {
            this.recursive = builder.recursive;
        } else throw new NullPointerException("Recursive config cant be null");
    }

    @NotNull
    public String getProjectSet() {
        return projectSet;
    }

    @NotNull
    public String[] getDependencySet() {
        return dependencySet;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public static class Builder implements SourceSetMapperBuilder {
        private String projectSet;
        private String[] dependencySet;
        private Boolean recursive = true;

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
