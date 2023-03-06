package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.project.ManagerBase;
import com.srdjanv.localgitdependency.project.ProjectInstances;
import com.srdjanv.localgitdependency.tasks.buildtasks.BuildAllGitDependencies;
import com.srdjanv.localgitdependency.tasks.buildtasks.BuildGitDependency;
import com.srdjanv.localgitdependency.tasks.printtasks.PrintAllDependenciesInfo;
import com.srdjanv.localgitdependency.tasks.printtasks.PrintDependencyInfo;
import com.srdjanv.localgitdependency.tasks.undotasks.UndoAllLocalGitChanges;
import com.srdjanv.localgitdependency.tasks.undotasks.UndoLocalGitChanges;
import org.gradle.api.Project;
import org.gradle.api.Task;

// TODO: 05/03/2023 fix tasks
public class TasksManager extends ManagerBase {

    public TasksManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    public void initTasks() {
        createTask(getProject(), Constants.UNDO_ALL_LOCAL_GIT_CHANGES, UndoAllLocalGitChanges.class);
        createTask(getProject(), Constants.BUILD_ALL_GIT_DEPENDENCIES, BuildAllGitDependencies.class);
        createTask(getProject(), Constants.PRINT_ALL_DEPENDENCIES_INFO, PrintAllDependenciesInfo.class);

        for (Dependency dependency : getDependencyManager().getDependencies()) {
            UndoLocalGitChanges undoLocalGitChanges = createTask(getProject(), Constants.UNDO_LOCAL_GIT_CHANGES.apply(dependency.getName()), UndoLocalGitChanges.class);
            undoLocalGitChanges.setDependency(dependency);

            BuildGitDependency buildGitDependency = createTask(getProject(), Constants.BUILD_GIT_DEPENDENCY.apply(dependency.getName()), BuildGitDependency.class);
            buildGitDependency.setDependency(dependency);

            PrintDependencyInfo printDependencyInfo = createTask(getProject(), Constants.PRINT_DEPENDENCY_INFO.apply(dependency.getName()), PrintDependencyInfo.class);
            printDependencyInfo.setDependency(dependency);
        }
    }

    private <T extends Task> T createTask(Project project, String name, Class<T> taskClass) {
        T task = project.getTasks().create(name, taskClass);
        task.setGroup(Constants.EXTENSION_NAME);
        return task;
    }

}
