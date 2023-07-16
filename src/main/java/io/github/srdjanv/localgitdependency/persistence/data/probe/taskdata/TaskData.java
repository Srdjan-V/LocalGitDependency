package io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata;

import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import io.github.srdjanv.localgitdependency.util.ClassUtil;

@NonNullData
public class TaskData extends TaskDataFields {
    public TaskData() {
    }

    private TaskData(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, TaskDataFields.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends TaskDataFields {
        private Builder() {
        }

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
