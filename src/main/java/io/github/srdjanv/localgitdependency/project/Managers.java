package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public interface Managers {
    Project getProject();
    ProjectManager getProjectManager();
    PropertyManager getPropertyManager();
    IDependencyManager getDependencyManager();
    GitManager getGitManager();
    IGradleManager getGradleManager();
    PersistenceManager getPersistenceManager();
    TasksManager getTasksManager();
    LocalGitDependencyExtension getLocalGitDependencyExtension();
    CleanupManager getCleanupManager();
}
