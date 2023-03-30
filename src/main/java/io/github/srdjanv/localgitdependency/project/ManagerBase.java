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

public abstract class ManagerBase implements Manager {
    private final Managers managers;

    public ManagerBase(Managers managers) {
        this.managers = managers;
    }

    protected abstract void managerConstructor();

    @Override
    public Managers getProjectManagers() {
        return managers;
    }

    @Override
    public String getManagerName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Project getProject() {
        return managers.getProject();
    }

    @Override
    public IProjectManager getProjectManager() {
        return managers.getProjectManager();
    }

    @Override
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return managers.getLocalGitDependencyExtension();
    }

    @Override
    public IDependencyManager getDependencyManager() {
        return managers.getDependencyManager();
    }

    @Override
    public IGradleManager getGradleManager() {
        return managers.getGradleManager();
    }

    @Override
    public IPropertyManager getPropertyManager() {
        return managers.getPropertyManager();
    }

    @Override
    public IGitManager getGitManager() {
        return managers.getGitManager();
    }

    @Override
    public IPersistenceManager getPersistenceManager() {
        return managers.getPersistenceManager();
    }

    @Override
    public ITasksManager getTasksManager() {
        return managers.getTasksManager();
    }

    @Override
    public ICleanupManager getCleanupManager() {
        return managers.getCleanupManager();
    }

}
