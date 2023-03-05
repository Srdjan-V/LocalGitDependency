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

public interface Managers {
    Project getProject();

    SettingsExtension getSettingsExtension();

    DependencyManager getDependencyManager();

    GradleManager getGradleManager();

    PropertyManager getPropertyManager();

    GitManager getGitManager();

    PersistenceManager getPersistenceManager();

    TasksManager getTasksManager();

    Logger getLogger();
}
