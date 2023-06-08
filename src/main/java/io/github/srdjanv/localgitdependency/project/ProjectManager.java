package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;

import java.util.ArrayList;
import java.util.List;

final class ProjectManager extends ManagerBase implements IProjectManager {
    private static final List<ManagerRunner<?>> PROJECT_RUNNERS;

    public static final ManagerRunner<IPersistenceManager> savePersistentDataTask =
            ManagerRunner.create(managerRunner -> {
                managerRunner.setManagerSupplier(Managers::getPersistenceManager);
                managerRunner.setTask(clazz -> clazz.getDeclaredMethod("savePersistentData"));
            });

    static {
        PROJECT_RUNNERS = new ArrayList<>();
        PROJECT_RUNNERS.add(ManagerRunner.<IConfigManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getConfigManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("createEssentialDirectories"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ICleanupManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getCleanupManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("init"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IPersistenceManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getPersistenceManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("loadPersistentData"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGitManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGitManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initRepos"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initGradleAPI"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("startBuildTasks"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IDependencyManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getDependencyManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("addBuiltDependencies"));
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ITasksManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getTasksManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initTasks"));
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
        Throwable throwable = null;
        String name = getProject().getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        final long start = System.currentTimeMillis();
        PluginLogger.startInfo("{} starting {} tasks", formattedName, Constants.EXTENSION_NAME);
        try {
            for (ManagerRunner<?> projectRunner : PROJECT_RUNNERS) {
                projectRunner.runAndLog(getProjectManagers());
            }
        } catch (Throwable e) {
            throwable = e;
        } finally {
            try {
                savePersistentDataTask.runAndLog(getProjectManagers());
            } catch (Throwable suppressed) {
                if (throwable == null) {
                    throwable = suppressed;
                } else {
                    throwable.addSuppressed(suppressed);
                }
            }
            final long spent = System.currentTimeMillis() - start;
            PluginLogger.startInfo("{} finished {} tasks in {} ms", formattedName, Constants.EXTENSION_NAME, spent);
        }
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
    }

}
