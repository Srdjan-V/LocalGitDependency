package io.github.srdjanv.localgitdependency.extentions;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.IPropertyManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import org.gradle.api.Project;

public class LocalGitDependencyManagerInstance implements Managers {
    private final IProjectManager manager;

    public LocalGitDependencyManagerInstance(Project project) {
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
    public IPropertyManager getPropertyManager() {
        return manager.getPropertyManager();
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
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return manager.getLocalGitDependencyExtension();
    }

    @Override
    public ICleanupManager getCleanupManager() {
        return manager.getCleanupManager();
    }
}
