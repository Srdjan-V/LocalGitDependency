package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.property.IPropertyManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;

import java.util.ArrayList;
import java.util.List;

class ProjectManager extends ManagerBase implements IProjectManager {
    private static final List<ManagerRunner<?>> PROJECT_RUNNERS;

    static {
        PROJECT_RUNNERS = new ArrayList<>();
        PROJECT_RUNNERS.add(ManagerRunner.<IPropertyManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getPropertyManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("createEssentialDirectories"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ICleanupManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getCleanupManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("init"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IPersistenceManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getPersistenceManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("loadPersistentData"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGitManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGitManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("initRepos"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("initGradleAPI"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IPersistenceManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getPersistenceManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("savePersistentData"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("buildDependencies"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IDependencyManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getDependencyManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("addBuiltDependencies"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ITasksManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getTasksManager);
            managerRunner.setTask(clazz-> clazz.getDeclaredMethod("initTasks"));
        }));
    }

    ProjectManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void startPlugin() {
        String name = getProject().getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        final long start = System.currentTimeMillis();
        PluginLogger.startInfo("{} starting {} tasks", formattedName, Constants.EXTENSION_NAME);
        for (ManagerRunner<?> projectRunner : PROJECT_RUNNERS) {
            projectRunner.runAndLog(getProjectManagers());
        }
        final long spent = System.currentTimeMillis() - start;
        PluginLogger.startInfo("{} finished {} tasks in {} ms", formattedName, Constants.EXTENSION_NAME, spent);
    }

}
