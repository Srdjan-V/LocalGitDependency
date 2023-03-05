package com.srdjanv.localgitdependency.project;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.DependencyManager;
import com.srdjanv.localgitdependency.git.GitManager;
import com.srdjanv.localgitdependency.gradle.GradleManager;
import com.srdjanv.localgitdependency.persistence.PersistenceManager;
import com.srdjanv.localgitdependency.property.PropertyManager;
import com.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ProjectManager extends ManagerBase {

    private static final Map<File, ProjectManager> projectProjectInstanceMap = new HashMap<>();
    private static final List<ProjectRunner> PROJECT_RUNNERS;

    public static ProjectManager getProject(Project project) {
        return projectProjectInstanceMap.get(project.getProjectDir());
    }
    public static void createProject(Project project) {
        new ProjectManager(project);
    }

    private ProjectManager(Project project) {
        super(new ProjectBuilder(project));

        project.getPluginManager().apply("java");

        ProjectManager projectManager = projectProjectInstanceMap.get(project.getProjectDir());
        if (projectManager == null) {
            projectProjectInstanceMap.put(project.getProjectDir(), this);
        } else {
            projectManager.close();
            projectProjectInstanceMap.put(project.getProjectDir(), this);
        }

        project.afterEvaluate(p -> startPlugin(projectProjectInstanceMap.get(p)));
    }

    public static void startPlugin(ProjectManager projectManager) {
        long start = System.currentTimeMillis();
        projectManager.getLogger().info("Starting {} tasks", Constants.EXTENSION_NAME);
        PROJECT_RUNNERS.forEach(projectRunner -> projectRunner.runAndLog(projectManager));
        long spent = System.currentTimeMillis() - start;
        projectManager.getLogger().info("Finished {} tasks in {} ms", Constants.EXTENSION_NAME, spent);
    }

    static {
        PROJECT_RUNNERS = new LinkedList<>();
        try {
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getPropertyManager().createEssentialDirectories(),
                    PropertyManager.class.getDeclaredMethod("createEssentialDirectories")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getGitManager().initRepos(),
                    GitManager.class.getDeclaredMethod("initRepos")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getGradleManager().initGradleAPI(),
                    GradleManager.class.getDeclaredMethod("initGradleAPI")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getPersistenceManager().savePersistentData(),
                    PersistenceManager.class.getDeclaredMethod("savePersistentData")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getGradleManager().buildDependencies(),
                    GradleManager.class.getDeclaredMethod("buildDependencies")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getDependencyManager().addBuiltDependencies(),
                    DependencyManager.class.getDeclaredMethod("addBuiltDependencies")
            ));
            PROJECT_RUNNERS.add(new ProjectRunner(
                    (projectManager) -> projectManager.getTasksManager().initTasks(),
                    TasksManager.class.getDeclaredMethod("initTasks")
            ));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ProjectRunner {
        private final Consumer<ProjectManager> task;
        private final Method method;

        public ProjectRunner(Consumer<ProjectManager> task, Method method) {
            this.task = task;
            this.method = method;
        }

        public void runAndLog(ProjectManager projectManager) {
            long start = System.currentTimeMillis();
            projectManager.getLogger().info("{}: Starting task {}", method.getDeclaringClass().getSimpleName(), method.getName());
            task.accept(projectManager);
            long spent = System.currentTimeMillis() - start;
            projectManager.getLogger().info("{}: Finished task {} in {} ms", method.getDeclaringClass().getSimpleName(), method.getName(), spent);
        }
    }

}
