package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.property.IPropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

public interface Managers {
    Project getProject();
    IProjectManager getProjectManager();
    IPropertyManager getPropertyManager();
    IDependencyManager getDependencyManager();
    IGitManager getGitManager();
    IGradleManager getGradleManager();
    IPersistenceManager getPersistenceManager();
    TasksManager getTasksManager();
    LocalGitDependencyExtension getLocalGitDependencyExtension();
    ICleanupManager getCleanupManager();
}
