package io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

import java.util.List;

public class PublicationData extends PublicationDataFields implements NonNullData {

    public PublicationData() {
    }

    public PublicationData(Builder builder) {
        BuilderUtil.instantiateObjectWithBuilder(this, builder, PublicationDataFields.class);
    }

    public static class Builder extends PublicationDataFields {
        public Builder() {
        }

        public Builder setRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        public Builder setPublicationName(String publicationName) {
            this.publicationName = publicationName;
            return this;
        }

        public Builder setTasks(List<TaskData> tasks) {
            this.tasks = tasks;
            return this;
        }

        public PublicationData create() {
            return new PublicationData(this);
        }
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public List<TaskData> getTasks() {
        return tasks;
    }

}
