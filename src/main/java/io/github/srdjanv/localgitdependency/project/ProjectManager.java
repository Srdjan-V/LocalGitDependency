package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ProjectManager extends ManagerBase {
    private static final List<ManagerRunner> PROJECT_RUNNERS;

    static {
        PROJECT_RUNNERS = new LinkedList<>();
        try {
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getPropertyManager().createEssentialDirectories(),
                    PropertyManager.class.getDeclaredMethod("createEssentialDirectories")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getCleanupManager().init(),
                    CleanupManager.class.getDeclaredMethod("init")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getGitManager().initRepos(),
                    GitManager.class.getDeclaredMethod("initRepos")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getGradleManager().initGradleAPI(),
                    GradleManager.class.getDeclaredMethod("initGradleAPI")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getPersistenceManager().savePersistentData(),
                    PersistenceManager.class.getDeclaredMethod("savePersistentData")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getGradleManager().buildDependencies(),
                    GradleManager.class.getDeclaredMethod("buildDependencies")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getDependencyManager().addBuiltDependencies(),
                    DependencyManager.class.getDeclaredMethod("addBuiltDependencies")
            ));
            PROJECT_RUNNERS.add(new ManagerRunner(
                    projectManager -> projectManager.getTasksManager().initTasks(),
                    TasksManager.class.getDeclaredMethod("initTasks")
            ));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProjectManager createProject(Project project) {
        return new ProjectManager(project);
    }

    private ProjectManager(Project project) {
        super(new ProjectInstances(project));
        this.managerConstructor();
    }

    @Override
    protected void managerConstructor() {
    }

    public void startPlugin() {
        String name = getProject().getName();
        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        long start = System.currentTimeMillis();
        PluginLogger.startInfo("{} starting {} tasks", formattedName, Constants.EXTENSION_NAME);
        PROJECT_RUNNERS.forEach(projectRunner -> projectRunner.runAndLog(this));
        long spent = System.currentTimeMillis() - start;
        PluginLogger.startInfo("{} finished {} tasks in {} ms", formattedName, Constants.EXTENSION_NAME, spent);
    }

    private static class ManagerRunner {
        private final Consumer<ProjectManager> task;
        private final Method method;

        public ManagerRunner(Consumer<ProjectManager> task, Method method) {
            this.task = task;
            this.method = method;
        }

        public void runAndLog(ProjectManager projectManager) {
            long start = System.currentTimeMillis();
            PluginLogger.info("{}: Starting task {}", method.getDeclaringClass().getSimpleName(), method.getName());
            task.accept(projectManager);
            long spent = System.currentTimeMillis() - start;
            PluginLogger.info("{}: Finished task {} in {} ms", method.getDeclaringClass().getSimpleName(), method.getName(), spent);
        }
    }
}
