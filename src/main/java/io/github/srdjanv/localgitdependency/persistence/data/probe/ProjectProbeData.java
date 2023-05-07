package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;
import org.gradle.api.JavaVersion;

import java.util.List;

public class ProjectProbeData extends ProjectProbeDataFields implements NonNullData {
    public ProjectProbeData() {
    }

    private ProjectProbeData(Builder builder) {
        BuilderUtil.instantiateObjectWithBuilder(this, builder, ProjectProbeDataFields.class);
    }

    public static class Builder extends ProjectProbeDataFields{
        public Builder() {
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder setArchivesBaseName(String archivesBaseName) {
            this.archivesBaseName = archivesBaseName;
            return this;
        }

        public Builder setProjectGradleVersion(String projectGradleVersion) {
            this.projectGradleVersion = projectGradleVersion;

            return this;
        }

        public Builder setJavaVersion(JavaVersion javaVersion) {
            this.javaVersion = javaVersion;
            return this;
        }

        public Builder setCanProjectUseWithSourcesJar(boolean canProjectUseWithSourcesJar) {
            this.canProjectUseWithSourcesJar = canProjectUseWithSourcesJar;
            return this;
        }

        public Builder setCanProjectUseWithJavadocJar(boolean canProjectUseWithJavadocJar) {
            this.canProjectUseWithJavadocJar = canProjectUseWithJavadocJar;
            return this;
        }

        public Builder setArtifactTasks(List<TaskData> artifactTasks) {
            this.artifactTasks = artifactTasks;
            return this;
        }

        public Builder setSourceSetsData(List<SourceSetData> sourceSetsData) {
            this.sourceSetsData = sourceSetsData;
            return this;
        }

        public Builder setPublicationData(PublicationData publicationData) {
            this.publicationData = publicationData;
            return this;
        }

        public Builder setRepositoryList(List<Repository> repositoryList) {
            this.repositoryList = repositoryList;
            return this;
        }

        public ProjectProbeData create() {
            return new ProjectProbeData(this);
        }
    }

    public String getVersion() {
        return version;
    }

    public String getProjectId() {
        return projectId;
    }
    public String getArchivesBaseName() {
        return archivesBaseName;
    }

    public String getProjectGradleVersion() {
        return projectGradleVersion;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public boolean isCanProjectUseWithSourcesJar() {
        return canProjectUseWithSourcesJar;
    }

    public boolean isCanProjectUseWithJavadocJar() {
        return canProjectUseWithJavadocJar;
    }

    public List<TaskData> getArtifactTasks() {
        return artifactTasks;
    }

    public List<SourceSetData> getSourceSetsData() {
        return sourceSetsData;
    }

    public PublicationData getPublicationData() {
        return publicationData;
    }

    public List<Repository> getRepositoryList() {
        return repositoryList;
    }
}
