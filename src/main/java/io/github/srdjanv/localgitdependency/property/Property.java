package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.git.GitInfo;

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

    public Property(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        generatedJarsToAdd = builder.generatedJarsToAdd;
        generatedArtifactNames = builder.generatedArtifactNames;
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

    public static class Builder extends CommonPropertyBuilder {
        private final String url;
        private String name;
        private String target;
        private GitInfo.TargetType targetType;
        private List<String> generatedJarsToAdd;
        private List<String> generatedArtifactNames;

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
         *
         * @param generatedArtifactNames Targeted artifacts
         */
        public void generatedArtifactNames(List<String> generatedArtifactNames) {
            this.generatedArtifactNames = generatedArtifactNames;
        }

    }
}
