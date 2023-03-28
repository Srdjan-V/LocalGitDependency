package io.github.srdjanv.localgitdependency.tasks;

import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public interface ITasksManager extends Managers {
    static ITasksManager createInstance(ProjectInstances projectInstances) {
        return new TasksManager(projectInstances);
    }
    void initTasks();
}
