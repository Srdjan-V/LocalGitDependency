package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class TasksManager {

    public void initTasks() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            UndoLocalGitChanges undoLocalGitChanges = createTask(Instances.getProject(), Constants.UNDO_LOCAL_GIT_CHANGES.apply(dependency.getName()), UndoLocalGitChanges.class);
            undoLocalGitChanges.setDependency(dependency);
            undoLocalGitChanges.createDescription();

            BuildGitDependency buildGitDependency = createTask(Instances.getProject(), Constants.BUILD_GIT_DEPENDENCY.apply(dependency.getName()), BuildGitDependency.class);
            buildGitDependency.setDependency(dependency);
            buildGitDependency.createDescription();

            PrintDependencyInfo printDependencyInfo = createTask(Instances.getProject(), Constants.PRINT_DEPENDENCY_INFO.apply(dependency.getName()), PrintDependencyInfo.class);
            printDependencyInfo.setDependency(dependency);
            printDependencyInfo.createDescription();
        }
    }

    private <T extends Task> T createTask(Project project, String name, Class<T> taskClass) {
        T task = project.getTasks().create(name, taskClass);
        task.setGroup(Constants.EXTENSION_NAME);
        return task;
    }

}
