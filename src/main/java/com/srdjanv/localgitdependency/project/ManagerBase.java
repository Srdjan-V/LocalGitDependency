package com.srdjanv.localgitdependency.project;

import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.depenency.DependencyManager;
import com.srdjanv.localgitdependency.extentions.SettingsExtension;
import com.srdjanv.localgitdependency.git.GitManager;
import com.srdjanv.localgitdependency.gradle.GradleManager;
import com.srdjanv.localgitdependency.persistence.PersistenceManager;
import com.srdjanv.localgitdependency.property.PropertyManager;
import com.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public abstract class ManagerBase implements Managers, AutoCloseable {
    private final Project project;
    private final PropertyManager propertyManager;
    private final DependencyManager dependencyManager;
    private final GitManager gitManager;
    private final GradleManager gradleManager;
    private final PersistenceManager persistenceManager;
    private final TasksManager tasksManager;
    private final SettingsExtension settingsExtension;
    private final Logger logger;

    public ManagerBase(ProjectBuilder projectBuilder) {
        this.project = projectBuilder.project;
        this.propertyManager = projectBuilder.propertyManager;
        this.dependencyManager = projectBuilder.dependencyManager;
        this.gitManager = projectBuilder.gitManager;
        this.gradleManager = projectBuilder.gradleManager;
        this.persistenceManager = projectBuilder.persistenceManager;
        this.tasksManager = projectBuilder.tasksManager;
        this.settingsExtension = projectBuilder.settingsExtension;
        this.logger = projectBuilder.logger;
    }


    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public SettingsExtension getSettingsExtension() {
        return settingsExtension;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public GradleManager getGradleManager() {
        return gradleManager;
    }

    @Override
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    @Override
    public GitManager getGitManager() {
        return gitManager;
    }

    @Override
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    @Override
    public TasksManager getTasksManager() {
        return tasksManager;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void close() {
        gradleManager.disconnectAllGradleConnectors();
    }
}
