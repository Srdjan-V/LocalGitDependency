package io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;

import java.util.function.Consumer;

public class TaskData implements TaskDataGetters, TaskDataSetters, NonNullData {
    private String name;
    private String classifier;

    public TaskData() {
    }

    public static TaskData create(Consumer<TaskDataSetters> configuration) {
        TaskData instance = new TaskData();
        configuration.accept(instance);
        return instance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
}
