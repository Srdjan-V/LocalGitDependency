package io.github.srdjanv.localgitdependency.injection.model;

import java.util.List;

public interface PublishingObject {
    String getRepositoryName();
    String getPublicationName();
    List<TaskObject> getTasks();
}
