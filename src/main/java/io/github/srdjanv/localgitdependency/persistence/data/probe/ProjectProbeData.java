package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.JavaVersion;

import java.util.List;
import java.util.function.Consumer;

public class ProjectProbeData implements ProjectProbeDataGetters, ProjectProbeDataSetters, NonNullData {
    public String version;
    private String projectId;
    private String projectGradleVersion;
    private JavaVersion javaVersion;
    private boolean canProjectUseWithSourcesJar;
    private boolean canProjectUseWithJavadocJar;
    private boolean hasJavaPlugin;
    private boolean hasMavenPublishPlugin;
    private List<TaskData> appropriateTasks;
    private List<SourceSetData> defaultSourceSets;
    private PublicationData publicationObject;
    private List<Repository> repositoryList;

    public ProjectProbeData() {
    }

    public static ProjectProbeDataGetters create(Consumer<ProjectProbeDataSetters> configuration) {
        ProjectProbeData instance = new ProjectProbeData();
        configuration.accept(instance);
        return instance;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String getProjectGradleVersion() {
        return projectGradleVersion;
    }

    @Override
    public void setProjectGradleVersion(String projectGradleVersion) {
        this.projectGradleVersion = projectGradleVersion;
    }

    @Override
    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    @Override
    public void setJavaVersion(JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    @Override
    public boolean isCanProjectUseWithSourcesJar() {
        return canProjectUseWithSourcesJar;
    }

    @Override
    public void setCanProjectUseWithSourcesJar(boolean canProjectUseWithSourcesJar) {
        this.canProjectUseWithSourcesJar = canProjectUseWithSourcesJar;
    }

    @Override
    public boolean isCanProjectUseWithJavadocJar() {
        return canProjectUseWithJavadocJar;
    }

    @Override
    public void setCanProjectUseWithJavadocJar(boolean canProjectUseWithJavadocJar) {
        this.canProjectUseWithJavadocJar = canProjectUseWithJavadocJar;
    }

    @Override
    public boolean isHasJavaPlugin() {
        return hasJavaPlugin;
    }

    @Override
    public void setHasJavaPlugin(boolean hasJavaPlugin) {
        this.hasJavaPlugin = hasJavaPlugin;
    }

    @Override
    public boolean isHasMavenPublishPlugin() {
        return hasMavenPublishPlugin;
    }

    @Override
    public void setHasMavenPublishPlugin(boolean hasMavenPublishPlugin) {
        this.hasMavenPublishPlugin = hasMavenPublishPlugin;
    }

    @Override
    public List<TaskData> getTaskData() {
        return appropriateTasks;
    }

    @Override
    public void setTaskData(List<TaskData> appropriateTasks) {
        this.appropriateTasks = appropriateTasks;
    }

    @Override
    public List<SourceSetData> getSourceSetData() {
        return defaultSourceSets;
    }

    @Override
    public void setSourceSetData(List<SourceSetData> defaultSourceSets) {
        this.defaultSourceSets = defaultSourceSets;
    }

    @Override
    public PublicationData getPublicationData() {
        return publicationObject;
    }

    @Override
    public void setPublicationData(PublicationData publicationData) {
        this.publicationObject = publicationData;
    }

    @Override
    public List<Repository> getRepositoryList() {
        return repositoryList;
    }

    @Override
    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }
}
