package io.github.srdjanv.localgitdependency.extentions;

import io.github.srdjanv.localgitdependency.project.IProjectManager;
import org.gradle.api.Project;

public class LocalGitDependencyManagerInstance {
    private final IProjectManager manager;

    public LocalGitDependencyManagerInstance(Project project) {
        this.manager = IProjectManager.createProject(project);
    }

    public IProjectManager getManager() {
        return manager;
    }

    public void startPlugin() {
        manager.startPlugin();
    }
}
