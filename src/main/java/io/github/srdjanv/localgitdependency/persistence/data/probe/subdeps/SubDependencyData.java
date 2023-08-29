package io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.ClassUtil;

public class SubDependencyData extends SubDependencyFields{

    private SubDependencyData(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, SubDependencyFields.class);
    }

    public static SubDependencyData.Builder builder() {
        return new SubDependencyData.Builder();
    }

    public static class Builder extends SubDependencyFields {
        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setProjectID(String projectID) {
            this.projectID = projectID;
            return this;
        }


        public Builder setArchivesBaseName(String archivesBaseName) {
            this.archivesBaseName = archivesBaseName;
            return this;
        }

        public Builder setDependencyType(Dependency.Type dependecyType) {
            this.dependencyType = dependecyType;
            return this;
        }

        public Builder setGitDir(String gitDir) {
            this.gitDir = gitDir;
            return this;
        }

        public SubDependencyData create() {
            return new SubDependencyData(this);
        }

    }

    public String getName() {
        return name;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getArchivesBaseName() {
        return archivesBaseName;
    }

    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    public String getGitDir() {
        return gitDir;
    }

}
