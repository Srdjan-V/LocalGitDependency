package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.ideintegration.IIDEManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
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
    public IDependencyManager getDependencyManager() {
        return managers.getDependencyManager();
    }

    @Override
    public IIDEManager getIDEManager() {
        return managers.getIDEManager();
    }

    @Override
    public IGradleManager getGradleManager() {
        return managers.getGradleManager();
    }

    @Override
    public IConfigManager getConfigManager() {
        return managers.getConfigManager();
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

    @Override
    public <T> T getLGDExtensionByType(Class<T> type) {
        return managers.getLGDExtensionByType(type);
    }
}
