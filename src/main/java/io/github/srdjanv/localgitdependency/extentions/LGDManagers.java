package io.github.srdjanv.localgitdependency.extentions;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import org.gradle.api.Project;

public final class LGDManagers implements Managers {
    private final IProjectManager manager;

    public LGDManagers(Project project) {
        this.manager = IProjectManager.createProject(project);
    }

    @Override
    public Project getProject() {
        return manager.getProject();
    }

    @Override
    public IProjectManager getProjectManager() {
        return manager.getProjectManager();
    }

    @Override
    public IConfigManager getConfigManager() {
        return manager.getConfigManager();
    }

    @Override
    public IDependencyManager getDependencyManager() {
        return manager.getDependencyManager();
    }

    @Override
    public IGitManager getGitManager() {
        return manager.getGitManager();
    }

    @Override
    public IGradleManager getGradleManager() {
        return manager.getGradleManager();
    }

    @Override
    public IPersistenceManager getPersistenceManager() {
        return manager.getPersistenceManager();
    }

    @Override
    public ITasksManager getTasksManager() {
        return manager.getTasksManager();
    }

    @Override
    public ICleanupManager getCleanupManager() {
        return manager.getCleanupManager();
    }

    @Override
    public <T> T getExtensionByType(Class<T> type) {
        return manager.getExtensionByType(type);
    }
}
