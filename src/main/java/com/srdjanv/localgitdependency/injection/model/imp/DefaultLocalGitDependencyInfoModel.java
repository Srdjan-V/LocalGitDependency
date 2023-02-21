package com.srdjanv.localgitdependency.injection.model.imp;

import com.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import com.srdjanv.localgitdependency.injection.model.PublishingObject;
import com.srdjanv.localgitdependency.injection.model.TaskObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultLocalGitDependencyInfoModel implements LocalGitDependencyInfoModel, Serializable {
    public long versionUID = serialVersionUID;
    private final String projectId;
    private final String projectGradleVersion;
    private final boolean hasJavaPlugin;
    private final boolean hasMavenPublishPlugin;
    private final List<DefaultTaskObject> appropriateTasks;
    private final DefaultPublishingObject publicationObject;


    public DefaultLocalGitDependencyInfoModel(
            String projectId, String projectGradleVersion, boolean hasJavaPlugin,
            boolean hasMavenPublishPlugin, List<DefaultTaskObject> appropriateTasks, DefaultPublishingObject defaultPublicationObject) {
        this.projectId = projectId;
        this.projectGradleVersion = projectGradleVersion;
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
