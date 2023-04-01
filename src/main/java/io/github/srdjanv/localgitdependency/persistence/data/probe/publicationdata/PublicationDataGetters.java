package io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata;

import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskDataGetters;

import java.util.List;

public interface PublicationDataGetters {
    String getRepositoryName();
    String getPublicationName();
    List<? extends TaskDataGetters> getTasks();
}
