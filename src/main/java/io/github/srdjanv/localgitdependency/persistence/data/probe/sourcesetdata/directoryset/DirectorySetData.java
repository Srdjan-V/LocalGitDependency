package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset;

import io.github.srdjanv.localgitdependency.ideintegration.adapters.Adapter;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import java.util.List;

@NonNullData
public class DirectorySetData extends DirectorySetDataFields {
    public DirectorySetData() {}

    private DirectorySetData(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, DirectorySetDataFields.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends DirectorySetDataFields {
        private Builder() {}

        public Builder setBuildClassesDir(String buildClassesDir) {
            this.buildClassesDir = buildClassesDir;
            return this;
        }

        public Builder setType(Adapter.Types type) {
            this.type = type;
            return this;
        }

        public Builder setSources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public DirectorySetData create() {
            return new DirectorySetData(this);
        }
    }

    public Adapter.Types getType() {
        return type;
    }

    public List<String> getSources() {
        return sources;
    }

    public String getBuildClassesDir() {
        return buildClassesDir;
    }
}
