package io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;

import java.util.List;
import java.util.function.Consumer;

public class PublicationData implements PublicationDataGetters, PublicationDataSetters, NonNullData {
    private String repositoryName;
    private String publicationName;
    private List<TaskData> tasks;

    public PublicationData() {
    }

    public static PublicationData create(Consumer<PublicationDataSetters> configuration) {
        PublicationData instance = new PublicationData();
        configuration.accept(instance);
        return instance;
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public String getPublicationName() {
        return publicationName;
    }

    @Override
    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    @Override
    public List<TaskData> getTasks() {
        return tasks;
    }

    @Override
    public void setTasks(List<TaskData> tasks) {
        this.tasks = tasks;
    }
}
