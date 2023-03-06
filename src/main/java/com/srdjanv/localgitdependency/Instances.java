package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.depenency.DependencyManager;
import com.srdjanv.localgitdependency.extentions.SettingsExtension;
import com.srdjanv.localgitdependency.git.GitManager;
import com.srdjanv.localgitdependency.gradle.GradleManager;
import com.srdjanv.localgitdependency.persistence.PersistenceManager;
import com.srdjanv.localgitdependency.property.PropertyManager;
import com.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public class Instances {
    private static Project project;
    private static SettingsExtension settingsExtension;
    private static DependencyManager dependencyManager;
    private static GradleManager gradleManager;
    private static PropertyManager propertyManager;
    private static GitManager gitManager;
    private static PersistenceManager persistenceManager;
    private static TasksManager tasksManager;

    public static Project getProject() {
        return project;
    }

    public static void setProject(Project settings) {
        Instances.project = settings;
    }

    public static SettingsExtension getSettingsExtension() {
        return settingsExtension;
    }

    public static void setSettingsExtension(SettingsExtension settingsExtension) {
        Instances.settingsExtension = settingsExtension;
    }

    public static DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public static void setDependencyManager(DependencyManager dependencyManager) {
        Instances.dependencyManager = dependencyManager;
    }

    public static GradleManager getGradleManager() {
        return gradleManager;
    }

    public static void setGradleManager(GradleManager gradleManager) {
        Instances.gradleManager = gradleManager;
    }

    public static PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public static void setPropertyManager(PropertyManager propertyManager) {
        Instances.propertyManager = propertyManager;
    }

    public static GitManager getGitManager() {
        return gitManager;
    }

    public static void setGitManager(GitManager gitManager) {
        Instances.gitManager = gitManager;
    }

    public static PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public static void setPersistenceManager(PersistenceManager persistenceManager) {
        Instances.persistenceManager = persistenceManager;
    }

    public static TasksManager getTasksManager() {
        return tasksManager;
    }

    public static void setTasksManager(TasksManager tasksManager) {
        Instances.tasksManager = tasksManager;
    }
}
