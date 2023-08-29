package io.github.srdjanv.localgitdependency.project;

import org.gradle.api.Project;

public interface IProjectManager extends Manager {
    static IProjectManager createProject(Project project) {
        return new ManagerInstances(project).getProjectManager();
    }

    static IProjectManager createInstance(Managers managers) {
        return new ProjectManager(managers);
    }

    void startPlugin();
}
