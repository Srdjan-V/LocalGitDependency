package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.cleanup.ICleanupManager;
import io.github.srdjanv.localgitdependency.config.IConfigManager;
import io.github.srdjanv.localgitdependency.depenency.IDependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LGD;
import io.github.srdjanv.localgitdependency.extentions.LGDHelper;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;
import io.github.srdjanv.localgitdependency.ideintegration.IIDEManager;
import io.github.srdjanv.localgitdependency.persistence.IPersistenceManager;
import io.github.srdjanv.localgitdependency.tasks.ITasksManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.Project;

final class ManagerInstances implements Managers {
    private final Project project;
    private final IProjectManager projectManager;
    private final IConfigManager configManager;
    private final IDependencyManager dependencyManager;
    private final IIDEManager iIdeManager;
    private final IGitManager gitManager;
    private final IGradleManager gradleManager;
    private final IPersistenceManager persistenceManager;
    private final ITasksManager tasksManager;
    private final ICleanupManager cleanupManager;

    private final Map<Class<?>, Object> extensions;

    ManagerInstances(Project project) {
        this.project = project;

        final List<ManagerBase> managerList = new ArrayList<>(8);
        managerList.add((ManagerBase) (projectManager = IProjectManager.createInstance(this)));
        managerList.add((ManagerBase) (dependencyManager = IDependencyManager.createInstance(this)));
        managerList.add((ManagerBase) (iIdeManager = IIDEManager.createInstance(this)));
        managerList.add((ManagerBase) (configManager = IConfigManager.createInstance(this)));
        managerList.add((ManagerBase) (gitManager = IGitManager.createInstance(this)));
        managerList.add((ManagerBase) (gradleManager = IGradleManager.createInstance(this)));
        managerList.add((ManagerBase) (persistenceManager = IPersistenceManager.createInstance(this)));
        managerList.add((ManagerBase) (tasksManager = ITasksManager.createInstance(this)));
        managerList.add((ManagerBase) (cleanupManager = ICleanupManager.createInstance(this)));

        for (ManagerBase managerBase : managerList) {
            managerBase.managerConstructor();
        }

        extensions = new HashMap<>(3);
        Object ext;
        ext = project.getExtensions().create(LGD.NAME, LGD.class, this);
        extensions.put(LGD.class, ext);

        ext = project.getDependencies().getExtensions().create(LGD.NAME, LGDHelper.class, this);
        extensions.put(LGDHelper.class, ext);

        ext = project.getExtensions().create(LGDIDE.NAME, LGDIDE.class, this);
        extensions.put(LGDIDE.class, ext);
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
    public IIDEManager getIDEManager() {
        return iIdeManager;
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
    public ICleanupManager getCleanupManager() {
        return cleanupManager;
    }

    @Override
    public <T> T getLGDExtensionByType(Class<T> type) {
        var ex = extensions.get(type);
        if (!type.isInstance(ex)) throw new IllegalStateException();
        return (T) ex;
    }
}
