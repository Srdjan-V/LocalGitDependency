package io.github.srdjanv.localgitdependency.tasks;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.buildtasks.BuildAllGitDependencies;
import io.github.srdjanv.localgitdependency.tasks.buildtasks.BuildGitDependency;
import io.github.srdjanv.localgitdependency.tasks.printtasks.PrintAllDependenciesInfo;
import io.github.srdjanv.localgitdependency.tasks.printtasks.PrintDependencyInfo;
import io.github.srdjanv.localgitdependency.tasks.probetasks.ProbeAllDependenciesTask;
import io.github.srdjanv.localgitdependency.tasks.probetasks.ProbeDependencyTask;
import io.github.srdjanv.localgitdependency.tasks.undotasks.UndoAllLocalGitChanges;
import io.github.srdjanv.localgitdependency.tasks.undotasks.UndoLocalGitChanges;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.util.GradleVersion;

class TasksManager extends ManagerBase implements ITasksManager {
    TasksManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void initTasks() {
        TaskCreator taskCreator;

        var gradleVersion = GradleVersion.version(getProject().getGradle().getGradleVersion());
        if (gradleVersion.compareTo(GradleVersion.version("4.9")) >= 0) {
            taskCreator = this::register;
        } else {
            taskCreator = this::createTask;
        }

        if (Boolean.TRUE.equals(getConfigManager().getPluginConfig().getGenerateGradleTasks())) {
            taskCreator.create(Constants.UNDO_ALL_LOCAL_GIT_CHANGES, UndoAllLocalGitChanges.class, getProjectManagers());
            taskCreator.create(Constants.PROBE_ALL_DEPENDENCIES, ProbeAllDependenciesTask.class, getProjectManagers());
            taskCreator.create(Constants.BUILD_ALL_GIT_DEPENDENCIES, BuildAllGitDependencies.class, getProjectManagers());
            taskCreator.create(Constants.PRINT_ALL_DEPENDENCIES_INFO, PrintAllDependenciesInfo.class, getProjectManagers());
        }

        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (!dependency.isGenerateGradleTasks()) continue;

            taskCreator.create(Constants.UNDO_LOCAL_GIT_CHANGES.apply(dependency), UndoLocalGitChanges.class, getProjectManagers(), dependency);
            taskCreator.create(Constants.PROBE_DEPENDENCY.apply(dependency), ProbeDependencyTask.class, getProjectManagers(), dependency);
            taskCreator.create(Constants.BUILD_GIT_DEPENDENCY.apply(dependency), BuildGitDependency.class, getProjectManagers(), dependency);
            taskCreator.create(Constants.PRINT_DEPENDENCY_INFO.apply(dependency), PrintDependencyInfo.class, getProjectManagers(), dependency);
        }
    }


    private interface TaskCreator {
        void create(String name, Class<? extends Task> taskClass, Object... constructorArgs);
    }

    private <T extends Task> void register(String name, Class<T> taskClass, Object... constructorArgs) {
        TaskProvider<T> taskProvider = getProject().getTasks().register(name, taskClass, constructorArgs);
        taskProvider.configure(configurationAction -> configurationAction.setGroup(Constants.EXTENSION_NAME));
    }

    private <T extends Task> void createTask(String name, Class<T> taskClass, Object... constructorArgs) {
        T task = getProject().getTasks().create(name, taskClass, constructorArgs);
        task.setGroup(Constants.EXTENSION_NAME);
    }

}
