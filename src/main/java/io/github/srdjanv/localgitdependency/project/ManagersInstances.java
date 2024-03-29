package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

final class ManagersInstances implements Managers {
    private final Project project;
    private final IProjectManager projectManager;
    private final IConfigManager configManager;
    private final IDependencyManager dependencyManager;
    private final IGitManager gitManager;
    private final IGradleManager gradleManager;
    private final IPersistenceManager persistenceManager;
    private final ITasksManager tasksManager;
    private final LocalGitDependencyExtension localGitDependencyExtension;
    private final ICleanupManager cleanupManager;

    ManagersInstances(Project project) {
        this.project = project;

        final List<ManagerBase> managerList = new ArrayList<>(9);
        managerList.add((ManagerBase) (projectManager = IProjectManager.createInstance(this)));
        managerList.add(localGitDependencyExtension = project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, LocalGitDependencyExtension.class, this));
        managerList.add((ManagerBase) (dependencyManager = IDependencyManager.createInstance(this)));
        managerList.add((ManagerBase) (configManager = IConfigManager.createInstance(this)));
        managerList.add((ManagerBase) (gitManager = IGitManager.createInstance(this)));
        managerList.add((ManagerBase) (gradleManager = IGradleManager.createInstance(this)));
        managerList.add((ManagerBase) (persistenceManager = IPersistenceManager.createInstance(this)));
        managerList.add((ManagerBase) (tasksManager = ITasksManager.createInstance(this)));
        managerList.add((ManagerBase) (cleanupManager = ICleanupManager.createInstance(this)));

        for (ManagerBase managerBase : managerList) {
            managerBase.managerConstructor();
        }
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public IProjectManager getProjectManager() {
        return projectManager;
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public IDependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public IGitManager getGitManager() {
        return gitManager;
    }

    @Override
    public IGradleManager getGradleManager() {
        return gradleManager;
    }

    @Override
    public IPersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    @Override
    public ITasksManager getTasksManager() {
        return tasksManager;
    }

    @Override
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return localGitDependencyExtension;
    }

    @Override
    public ICleanupManager getCleanupManager() {
        return cleanupManager;
    }
}
