package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import org.gradle.api.Project;

public interface Managers {
    Project getProject();
    IProjectManager getProjectManager();
    IConfigManager getConfigManager();
    IDependencyManager getDependencyManager();
    IGitManager getGitManager();
    IGradleManager getGradleManager();
    IPersistenceManager getPersistenceManager();
    ITasksManager getTasksManager();
    ICleanupManager getCleanupManager();
    <T> T getLGDExtensionByType(Class<T> type);
}
