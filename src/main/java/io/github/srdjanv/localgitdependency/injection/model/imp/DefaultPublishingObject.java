package io.github.srdjanv.localgitdependency.injection.model.imp;

import io.github.srdjanv.localgitdependency.injection.model.PublishingObject;
import io.github.srdjanv.localgitdependency.injection.model.TaskObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultPublishingObject implements PublishingObject, Serializable {
    private final String repositoryName;
    private final String publicationName;
    private final List<DefaultTaskObject> tasks;

    public DefaultPublishingObject(String repositoryName, String publicationName, List<DefaultTaskObject> tasks) {
        this.repositoryName = repositoryName;
        this.publicationName = publicationName;
        this.tasks = tasks;
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public String getPublicationName() {
        return publicationName;
    }

    @Override
    public List<TaskObject> getTasks() {
        return tasks.stream().map(defaultTaskObject -> (TaskObject) defaultTaskObject).collect(Collectors.toList());
    }
}
