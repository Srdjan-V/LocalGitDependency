package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.property.IPropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

class ProjectManager extends ManagerBase implements IProjectManager {
    private static final List<ManagerRunner> PROJECT_RUNNERS;

    static {
        PROJECT_RUNNERS = new LinkedList<>();
        try {
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getPropertyManager().createEssentialDirectories(),
                    IPropertyManager.class.getDeclaredMethod("createEssentialDirectories")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getCleanupManager().init(),
                    ICleanupManager.class.getDeclaredMethod("init")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getGitManager().initRepos(),
                    IGitManager.class.getDeclaredMethod("initRepos")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getGradleManager().initGradleAPI(),
                    IGradleManager.class.getDeclaredMethod("initGradleAPI")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getPersistenceManager().savePersistentData(),
                    IPersistenceManager.class.getDeclaredMethod("savePersistentData")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getGradleManager().buildDependencies(),
                    IGradleManager.class.getDeclaredMethod("buildDependencies")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getDependencyManager().addBuiltDependencies(),
                    IDependencyManager.class.getDeclaredMethod("addBuiltDependencies")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    managers -> managers.getTasksManager().initTasks(),
                    TasksManager.class.getDeclaredMethod("initTasks")
            ));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    ProjectManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void startPlugin() {
        String name = getProject().getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        long start = System.currentTimeMillis();
        PluginLogger.startInfo("{} starting {} tasks", formattedName, Constants.EXTENSION_NAME);
        PROJECT_RUNNERS.forEach(projectRunner -> projectRunner.runAndLog(getProjectInstances()));
        long spent = System.currentTimeMillis() - start;
        PluginLogger.startInfo("{} finished {} tasks in {} ms", formattedName, Constants.EXTENSION_NAME, spent);
    }

    private static class ManagerRunner {
        private final Consumer<Managers> task;
        private final Method method;

        public ManagerRunner(Consumer<Managers> task, Method method) {
            this.task = task;
            this.method = method;
        }

        public void runAndLog(Managers managers) {
            long start = System.currentTimeMillis();
            PluginLogger.info("{}: Starting task {}", method.getDeclaringClass().getSimpleName(), method.getName());
            task.accept(managers);
            long spent = System.currentTimeMillis() - start;
            PluginLogger.info("{}: Finished task {} in {} ms", method.getDeclaringClass().getSimpleName(), method.getName(), spent);
        }
    }
}
