package io.github.srdjanv.localgitdependency.tasks;

import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface ITasksManager extends Manager {
    static ITasksManager createInstance(Managers managers) {
        return new TasksManager(managers);
    }
    @TaskDescription("creating tasks")
    void initTasks();
}
