package io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata;

import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;

import java.util.List;

public interface PublicationDataSetters {
    void setRepositoryName(String repositoryName);
    void setPublicationName(String publicationName);
    void setTasks(List<TaskData> tasks);
}
