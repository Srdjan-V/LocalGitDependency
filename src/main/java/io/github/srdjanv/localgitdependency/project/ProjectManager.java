package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.ideintegration.IIDEManager;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.project.ManagerRunner.RunLogType;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

final class ProjectManager extends ManagerBase implements IProjectManager {
    private static final List<ManagerRunner<?>> PROJECT_RUNNERS;

    private static final ManagerRunner<IPersistenceManager> savePersistentDataTask =
            ManagerRunner.create(managerRunner -> {
                managerRunner.setManagerSupplier(Managers::getPersistenceManager);
                managerRunner.setTask(clazz -> clazz.getDeclaredMethod("savePersistentData"));
                managerRunner.setRunLogType(RunLogType.MINIMAL);
            });

    static {
        PROJECT_RUNNERS = new ArrayList<>(10);
        final Predicate<Managers> emptyDepsSkip = managers -> {
            return managers.getDependencyManager().getDependencies().isEmpty();
        };

        PROJECT_RUNNERS.add(ManagerRunner.<IConfigManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getConfigManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("finalizeConfigs"));
            managerRunner.setRunLogType(RunLogType.SILENT);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IDependencyManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getDependencyManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("resolveRegisteredDependencies"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ICleanupManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getCleanupManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("init"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IPersistenceManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getPersistenceManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("loadPersistentData"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGitManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGitManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initRepos"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initGradleAPI"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IGradleManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getGradleManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("startBuildTasks"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IDependencyManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getDependencyManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("registerRepos"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<IIDEManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getIDEManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("handelSourceSets"));
            managerRunner.setRunLogType(RunLogType.MINIMAL);
            managerRunner.addSkipCheck(emptyDepsSkip);
            managerRunner.addSkipCheck(managers -> {
                return managers.getLGDExtensionByType(LGDIDE.class).getMappers().isEmpty();
            });
        }));
        PROJECT_RUNNERS.add(ManagerRunner.<ITasksManager>create(managerRunner -> {
            managerRunner.setManagerSupplier(Managers::getTasksManager);
            managerRunner.setTask(clazz -> clazz.getDeclaredMethod("initTasks"));
            managerRunner.setRunLogType(RunLogType.SILENT);
        }));
    }

    ProjectManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {}

    @Override
    public void startPlugin() {
        Throwable throwable = null;
        String name = getProject().getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        if (getConfigManager().getPluginConfig().getDisablePluginExecution().get()) {
            PluginLogger.title("{} skipping all {} tasks", formattedName, Constants.PLUGIN_NAME);
            return;
        }

        final long start = System.currentTimeMillis();
        PluginLogger.title("{} starting {} tasks", formattedName, Constants.PLUGIN_NAME);
        try {
            for (ManagerRunner<?> projectRunner : PROJECT_RUNNERS) projectRunner.runAndLog(getProjectManagers());
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
            PluginLogger.title("{} finished {} tasks in {} ms", formattedName, Constants.PLUGIN_NAME, spent);
        }
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
    }
}
