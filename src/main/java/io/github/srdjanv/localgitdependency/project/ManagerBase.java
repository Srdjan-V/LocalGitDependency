package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public abstract class ManagerBase implements Managers {
    private final ProjectInstances projectInstances;

    public ManagerBase(ProjectInstances projectInstances) {
        this.projectInstances = projectInstances;
    }

    protected abstract void managerConstructor();

    public ProjectInstances getProjectInstances() {
        return projectInstances;
    }

    @Override
    public Project getProject() {
        return projectInstances.getProject();
    }

    @Override
    public ProjectManager getProjectManager() {
        return projectInstances.getProjectManager();
    }

    @Override
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return projectInstances.getLocalGitDependencyExtension();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return projectInstances.getDependencyManager();
    }

    @Override
    public IGradleManager getGradleManager() {
        return projectInstances.getGradleManager();
    }

    @Override
    public PropertyManager getPropertyManager() {
        return projectInstances.getPropertyManager();
    }

    @Override
    public GitManager getGitManager() {
        return projectInstances.getGitManager();
    }

    @Override
    public PersistenceManager getPersistenceManager() {
        return projectInstances.getPersistenceManager();
    }

    @Override
    public TasksManager getTasksManager() {
        return projectInstances.getTasksManager();
    }

    @Override
    public CleanupManager getCleanupManager() {
        return projectInstances.getCleanupManager();
    }

}
