package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.SettingsExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

import java.lang.reflect.Field;

public class ProjectInstances {
   private final Project project;
    private final PropertyManager propertyManager;
    private final DependencyManager dependencyManager;
    private final GitManager gitManager;
    private final GradleManager gradleManager;
    private final PersistenceManager persistenceManager;
    private final TasksManager tasksManager;
    private final SettingsExtension settingsExtension;
    private final CleanupManager cleanupManager;

    public ProjectInstances(Project project) {
        this.project = project;

        settingsExtension = project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, SettingsExtension.class, this);
        dependencyManager = new DependencyManager(this);
        propertyManager = new PropertyManager(this);
        gitManager = new GitManager(this);
        gradleManager = new GradleManager(this);
        persistenceManager = new PersistenceManager(this);
        tasksManager = new TasksManager(this);
        cleanupManager = new CleanupManager(this);
        constructMangers();
    }

    private void constructMangers() {
        for (Field field : ProjectInstances.class.getDeclaredFields()) {
            if (field.getType().getSuperclass() == ManagerBase.class) {
                try {
                    ManagerBase managerBase = (ManagerBase) field.get(this);
                    managerBase.managerConstructor();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Project getProject() {
        return project;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public GitManager getGitManager() {
        return gitManager;
    }

    public GradleManager getGradleManager() {
        return gradleManager;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }

    public SettingsExtension getSettingsExtension() {
        return settingsExtension;
    }

    public CleanupManager getCleanupManager() {
        return cleanupManager;
    }
}
