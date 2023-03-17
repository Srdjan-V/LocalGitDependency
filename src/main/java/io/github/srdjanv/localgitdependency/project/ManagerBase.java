package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public abstract class ManagerBase {
    private final ProjectInstances projectInstances;

    public ManagerBase(ProjectInstances projectInstances) {
        this.projectInstances = projectInstances;
    }

    protected abstract void managerConstructor();

    public ProjectInstances getProjectInstances() {
        return projectInstances;
    }

    public Project getProject() {
        return projectInstances.getProject();
    }

    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return projectInstances.getLocalGitDependencyExtension();
    }

    public DependencyManager getDependencyManager() {
        return projectInstances.getDependencyManager();
    }

    public GradleManager getGradleManager() {
        return projectInstances.getGradleManager();
    }

    public PropertyManager getPropertyManager() {
        return projectInstances.getPropertyManager();
    }

    public GitManager getGitManager() {
        return projectInstances.getGitManager();
    }

    public PersistenceManager getPersistenceManager() {
        return projectInstances.getPersistenceManager();
    }

    public TasksManager getTasksManager() {
        return projectInstances.getTasksManager();
    }

    public CleanupManager getCleanupManager() {
        return projectInstances.getCleanupManager();
    }

}
