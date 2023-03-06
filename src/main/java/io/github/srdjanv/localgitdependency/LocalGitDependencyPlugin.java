package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.SettingsExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class LocalGitDependencyPlugin implements Plugin<Project> {

    private static final List<AfterEvaluateTaskWrapper> taskRunners;

    static {
        taskRunners = new LinkedList<>();
        try {
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getPropertyManager().createEssentialDirectories(),
                    PropertyManager.class.getDeclaredMethod("createEssentialDirectories")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getGitManager().initRepos(),
                    GitManager.class.getDeclaredMethod("initRepos")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getGradleManager().initGradleAPI(),
                    GradleManager.class.getDeclaredMethod("initGradleAPI")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getPersistenceManager().savePersistentData(),
                    PersistenceManager.class.getDeclaredMethod("savePersistentData")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getGradleManager().buildDependencies(),
                    GradleManager.class.getDeclaredMethod("buildDependencies")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getDependencyManager().addBuiltDependencies(),
                    DependencyManager.class.getDeclaredMethod("addBuiltDependencies")
            ));
            taskRunners.add(new AfterEvaluateTaskWrapper(
                    () -> Instances.getTasksManager().initTasks(),
                    TasksManager.class.getDeclaredMethod("initTasks")
            ));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java");

        Instances.setProject(project);// TODO: 18/02/2023 make multiProject compatible? 
        Instances.setDependencyManager(new DependencyManager());
        Instances.setGradleManager(new GradleManager());
        Instances.setPropertyManager(new PropertyManager());
        Instances.setGitManager(new GitManager());
        Instances.setPersistenceManager(new PersistenceManager());
        Instances.setTasksManager(new TasksManager());
        Instances.setSettingsExtension(project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, SettingsExtension.class));

        project.afterEvaluate(p -> startPlugin());
    }

    static void startPlugin() {
        long start = System.currentTimeMillis();
        Logger.info("Starting {} tasks", Constants.EXTENSION_NAME);
        taskRunners.forEach(AfterEvaluateTaskWrapper::runAndLog);
        long spent = System.currentTimeMillis() - start;
        Logger.info("Finished {} tasks in {} ms", Constants.EXTENSION_NAME, spent);
    }

    private static class AfterEvaluateTaskWrapper {
        private final Runnable task;
        private final Method method;

        public AfterEvaluateTaskWrapper(Runnable task, Method method) {
            this.task = task;
            this.method = method;
        }

        public void runAndLog() {
            long start = System.currentTimeMillis();
            Logger.info("{}: Starting task {}", method.getDeclaringClass().getSimpleName(), method.getName());
            task.run();
            long spent = System.currentTimeMillis() - start;
            Logger.info("{}: Finished task {} in {} ms", method.getDeclaringClass().getSimpleName(), method.getName(), spent);
        }
    }
}
