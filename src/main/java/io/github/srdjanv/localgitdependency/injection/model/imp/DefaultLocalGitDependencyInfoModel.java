package io.github.srdjanv.localgitdependency.injection.model.imp;

import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.PublishingObject;
import io.github.srdjanv.localgitdependency.injection.model.TaskObject;
import org.gradle.api.JavaVersion;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultLocalGitDependencyInfoModel implements LocalGitDependencyInfoModel, Serializable {
    private final String projectId;
    private final String projectGradleVersion;
    private final JavaVersion javaVersion;
    private final boolean canProjectUseWithSourcesJar;
    private final boolean canProjectUseWithJavadocJar;
    private final boolean hasJavaPlugin;
    private final boolean hasMavenPublishPlugin;
    private final List<DefaultTaskObject> appropriateTasks;
    private final DefaultPublishingObject publicationObject;


    public DefaultLocalGitDependencyInfoModel(
            String projectId, String projectGradleVersion, JavaVersion javaVersion,
            boolean canProjectUseWithSourcesJar, boolean canProjectUseWithJavadocJar,
            boolean hasJavaPlugin, boolean hasMavenPublishPlugin,
            List<DefaultTaskObject> appropriateTasks, DefaultPublishingObject defaultPublicationObject) {
        this.projectId = projectId;
        this.projectGradleVersion = projectGradleVersion;
        this.javaVersion = javaVersion;
        this.canProjectUseWithSourcesJar = canProjectUseWithSourcesJar;
        this.canProjectUseWithJavadocJar = canProjectUseWithJavadocJar;
        this.hasJavaPlugin = hasJavaPlugin;
        this.hasMavenPublishPlugin = hasMavenPublishPlugin;
        this.appropriateTasks = appropriateTasks;
        this.publicationObject = defaultPublicationObject;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String projectGradleVersion() {
        return projectGradleVersion;
    }

    @Override
    public JavaVersion getProjectJavaVersion() {
        return javaVersion;
    }

    @Override
    public boolean canProjectUseWithSourcesJar() {
        return canProjectUseWithSourcesJar;
    }

    @Override
    public boolean canProjectUseWithJavadocJar() {
        return canProjectUseWithJavadocJar;
    }

    @Override
    public boolean hasJavaPlugin() {
        return hasJavaPlugin;
    }

    @Override
    public boolean hasMavenPublishPlugin() {
        return hasMavenPublishPlugin;
    }

    @Override
    public List<TaskObject> getAppropriateTasks() {
        return appropriateTasks.stream().map(defaultTaskObject -> (TaskObject) defaultTaskObject).collect(Collectors.toList());
    }

    @Override
    public PublishingObject getAppropriatePublication() {
        return publicationObject;
    }
}
