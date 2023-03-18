package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public interface Managers {
    Project getProject();
    ProjectManager getProjectManager();
    PropertyManager getPropertyManager();
    DependencyManager getDependencyManager();
    GitManager getGitManager();
    GradleManager getGradleManager();
    PersistenceManager getPersistenceManager();
    TasksManager getTasksManager();
    LocalGitDependencyExtension getLocalGitDependencyExtension();
    CleanupManager getCleanupManager();
}
