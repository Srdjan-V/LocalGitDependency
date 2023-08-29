package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

@NonNullData
public class SourceSetData extends SourceSetDataFields {

    public SourceSetData() {}

    public SourceSetData(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, SourceSetDataFields.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SourceSetDataFields {
        private Builder() {
            directorySets = new ArrayList<>();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setBuildResourcesDir(String buildResourcesDir) {
            this.buildResourcesDir = buildResourcesDir;
            return this;
        }

        public Builder setDependentSourceSets(Set<String> dependentSourceSets) {
            this.dependentSourceSets = dependentSourceSets;
            return this;
        }

        public Builder setCompileClasspath(List<String> compileClasspath) {
            this.compileClasspath = compileClasspath;
            return this;
        }

        public Builder setResources(List<String> resources) {
            this.resources = resources;
            return this;
        }

        public Builder addDirectorySet(DirectorySetData directorySet) {
            this.directorySets.add(directorySet);
            return this;
        }

        public SourceSetData create() {
            return new SourceSetData(this);
        }
    }

    public String getName() {
        return name;
    }

    @Nullable public String getBuildResourcesDir() {
        return buildResourcesDir;
    }

    public Set<String> getDependentSourceSets() {
        return dependentSourceSets;
    }

    public List<String> getCompileClasspath() {
        return compileClasspath;
    }

    public List<DirectorySetData> getDirectorySetData() {
        return directorySets;
    }

    public List<String> getResources() {
        return resources;
    }
}
