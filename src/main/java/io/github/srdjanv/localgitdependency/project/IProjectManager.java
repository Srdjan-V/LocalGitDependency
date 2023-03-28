package io.github.srdjanv.localgitdependency.project;

import org.gradle.api.Project;

public interface IProjectManager extends Managers {
    static IProjectManager createProject(Project project) {
        return new ProjectInstances(project).getProjectManager();
    }
    static IProjectManager createInstance(ProjectInstances projectInstances) {
        return new ProjectManager(projectInstances);
    }
    void startPlugin();
}
