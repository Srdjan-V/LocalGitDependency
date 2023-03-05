package com.srdjanv.localgitdependency.project;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.depenency.DependencyManager;
import com.srdjanv.localgitdependency.extentions.SettingsExtension;
import com.srdjanv.localgitdependency.git.GitManager;
import com.srdjanv.localgitdependency.gradle.GradleManager;
import com.srdjanv.localgitdependency.persistence.PersistenceManager;
import com.srdjanv.localgitdependency.property.PropertyManager;
import com.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public class ProjectBuilder {
    final Project project;
    final PropertyManager propertyManager;
    final DependencyManager dependencyManager;
    final GitManager gitManager;
    final GradleManager gradleManager;
    final PersistenceManager persistenceManager;
    final TasksManager tasksManager;
    final SettingsExtension settingsExtension;
    final Logger logger;

    public ProjectBuilder(Project project) {
        this.project = project;

        settingsExtension = project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, SettingsExtension.class, this);
        dependencyManager = new DependencyManager(this);
        propertyManager = new PropertyManager(this);
        gitManager = new GitManager(this);
        gradleManager = new GradleManager(this);
        persistenceManager = new PersistenceManager(this);
        tasksManager = new TasksManager(this);
        logger = new Logger(this);
    }

}
