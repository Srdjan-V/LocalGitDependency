package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

import java.util.List;

public class SourceSetData extends SourceSetDataFields implements NonNullData {

    public SourceSetData() {
    }

    public SourceSetData(Builder builder) {
        BuilderUtil.instantiateObjectWithBuilder(this, builder, SourceSetDataFields.class);
    }

    public static class Builder extends SourceSetDataFields {
        public Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setClasspathConfigurationName(String classpathConfigurationName) {
            this.classpathConfigurationName = classpathConfigurationName;
            return this;
        }

        public Builder setRepositoryClasspathDependencies(List<String> repositoryClasspathDependencies) {
            this.repositoryClasspathDependencies = repositoryClasspathDependencies;
            return this;
        }

        public Builder setFileClasspathDependencies(List<String> fileClasspathDependencies) {
            this.fileClasspathDependencies = fileClasspathDependencies;
            return this;
        }

        public Builder setSources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public SourceSetData create() {
            return new SourceSetData(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getClasspathConfigurationName() {
        return classpathConfigurationName;
    }

    public List<String> getRepositoryClasspathDependencies() {
        return repositoryClasspathDependencies;
    }

    public List<String> getFileClasspathDependencies() {
        return fileClasspathDependencies;
    }

    public List<String> getSources() {
        return sources;
    }

}
