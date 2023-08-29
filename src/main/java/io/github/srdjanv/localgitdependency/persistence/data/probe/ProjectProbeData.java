package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import org.gradle.api.JavaVersion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NonNullData
public class ProjectProbeData extends ProjectProbeDataFields {
    public ProjectProbeData() {
    }

    private ProjectProbeData(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, ProjectProbeDataFields.class);
    }

    public static class Builder extends ProjectProbeDataFields {
        public Builder() {
        }

        public Builder setPluginVersion(String version) {
            this.pluginVersion = version;
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

        public Builder setCanProjectUseWithSourcesJar(Boolean canProjectUseWithSourcesJar) {
            this.canProjectUseWithSourcesJar = canProjectUseWithSourcesJar;
            return this;
        }

        public Builder setCanProjectUseWithJavadocJar(Boolean canProjectUseWithJavadocJar) {
            this.canProjectUseWithJavadocJar = canProjectUseWithJavadocJar;
            return this;
        }

        public Builder setSourceSetsData(List<SourceSetData> sourceSetsData) {
            this.sourceSetsData = sourceSetsData;
            return this;
        }

        public Builder setSubDependencyData(List<SubDependencyData> subDependencyData) {
            this.subDependencyData = subDependencyData;
            return this;
        }

        public ProjectProbeData create() {
            return new ProjectProbeData(this);
        }
    }

    @Nullable
    public String getPluginVersion() {
        return pluginVersion;
    }

    public String getProjectID() {
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

    @Nullable
    public Boolean isCanProjectUseWithSourcesJar() {
        return canProjectUseWithSourcesJar;
    }

    @Nullable
    public Boolean isCanProjectUseWithJavadocJar() {
        return canProjectUseWithJavadocJar;
    }

    public List<SourceSetData> getSourceSetsData() {
        return sourceSetsData;
    }

    public List<SubDependencyData> getSubDependencyData() {
        return subDependencyData;
    }
}
