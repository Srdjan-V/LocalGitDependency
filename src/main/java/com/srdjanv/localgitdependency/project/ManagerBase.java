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

public abstract class ManagerBase {
    private final ProjectInstances projectInstances;

    public ManagerBase(ProjectInstances projectInstances) {
        this.projectInstances = projectInstances;
    }

    protected void managerConstructor(){}

    public Project getProject() {
        return projectInstances.project;
    }

    public SettingsExtension getSettingsExtension() {
        return projectInstances.settingsExtension;
    }

    public DependencyManager getDependencyManager() {
        return projectInstances.dependencyManager;
    }

    public GradleManager getGradleManager() {
        return projectInstances.gradleManager;
    }

    public PropertyManager getPropertyManager() {
        return projectInstances.propertyManager;
    }

    public GitManager getGitManager() {
        return projectInstances.gitManager;
    }

    public PersistenceManager getPersistenceManager() {
        return projectInstances.persistenceManager;
    }

    public TasksManager getTasksManager() {
        return projectInstances.tasksManager;
    }

    public Logger getLogger() {
        return projectInstances.logger;
    }

}
