package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.property.IPropertyManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
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
    public IProjectManager getProjectManager() {
        return projectInstances.getProjectManager();
    }

    @Override
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return projectInstances.getLocalGitDependencyExtension();
    }

    @Override
    public IDependencyManager getDependencyManager() {
        return projectInstances.getDependencyManager();
    }

    @Override
    public IGradleManager getGradleManager() {
        return projectInstances.getGradleManager();
    }

    @Override
    public IPropertyManager getPropertyManager() {
        return projectInstances.getPropertyManager();
    }

    @Override
    public IGitManager getGitManager() {
        return projectInstances.getGitManager();
    }

    @Override
    public IPersistenceManager getPersistenceManager() {
        return projectInstances.getPersistenceManager();
    }

    @Override
    public ITasksManager getTasksManager() {
        return projectInstances.getTasksManager();
    }

    @Override
    public ICleanupManager getCleanupManager() {
        return projectInstances.getCleanupManager();
    }

}
