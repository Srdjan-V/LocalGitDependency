package io.github.srdjanv.localgitdependency.tasks;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.buildtasks.BuildAllGitDependencies;
import io.github.srdjanv.localgitdependency.tasks.buildtasks.BuildGitDependency;
import io.github.srdjanv.localgitdependency.tasks.printtasks.PrintAllDependenciesInfo;
import io.github.srdjanv.localgitdependency.tasks.printtasks.PrintDependencyInfo;
import io.github.srdjanv.localgitdependency.tasks.undotasks.UndoAllLocalGitChanges;
import io.github.srdjanv.localgitdependency.tasks.undotasks.UndoLocalGitChanges;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

import java.util.Arrays;

public class TasksManager {

    public void initTasks() {
        TaskCreator taskCreator;
        int[] gradleVersion = Arrays.stream(Instances.getProject().getGradle().getGradleVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (gradleVersion[0] >= 4 && gradleVersion[1] >= 9) {
            taskCreator = this::register;
        } else {
            taskCreator = this::createTask;
        }

        if (Instances.getPropertyManager().getGlobalProperty().getGenerateDefaultGradleTasks()) {
            taskCreator.create(Constants.UNDO_ALL_LOCAL_GIT_CHANGES, UndoAllLocalGitChanges.class);
            taskCreator.create(Constants.BUILD_ALL_GIT_DEPENDENCIES, BuildAllGitDependencies.class);
            taskCreator.create(Constants.PRINT_ALL_DEPENDENCIES_INFO, PrintAllDependenciesInfo.class);
        }

        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (!dependency.isGenerateGradleTasks()) continue;

            taskCreator.create(Constants.UNDO_LOCAL_GIT_CHANGES.apply(dependency.getName()), UndoLocalGitChanges.class, dependency);
            taskCreator.create(Constants.BUILD_GIT_DEPENDENCY.apply(dependency.getName()), BuildGitDependency.class, dependency);
            taskCreator.create(Constants.PRINT_DEPENDENCY_INFO.apply(dependency.getName()), PrintDependencyInfo.class, dependency);
        }
    }


    private interface TaskCreator {
        void create(String name, Class<? extends Task> taskClass, Object... constructorArgs);
    }

    private <T extends Task> void register(String name, Class<T> taskClass, Object... constructorArgs) {
        TaskProvider<T> taskProvider = Instances.getProject().getTasks().register(name, taskClass, constructorArgs);
        taskProvider.configure(configurationAction -> configurationAction.setGroup(Constants.EXTENSION_NAME));
    }

    private <T extends Task> void createTask(String name, Class<T> taskClass, Object... constructorArgs) {
        T task = Instances.getProject().getTasks().create(name, taskClass, constructorArgs);
        task.setGroup(Constants.EXTENSION_NAME);
    }

}
