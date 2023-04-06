package io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;
import io.github.srdjanv.localgitdependency.util.BuilderUtil;

public class TaskData extends TaskDataFields implements NonNullData {
    public TaskData() {
    }

    private TaskData(Builder builder) {
        BuilderUtil.instantiateObjectWithBuilder(this, builder, TaskDataFields.class);
    }

    public static class Builder extends TaskDataFields {

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setClassifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public TaskData create() {
            return new TaskData(this);
        }

    }

    public String getName() {
        return name;
    }

    public String getClassifier() {
        return classifier;
    }

}
