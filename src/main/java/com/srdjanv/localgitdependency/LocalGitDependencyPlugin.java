package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.project.ProjectManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LocalGitDependencyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ProjectManager.createProject(project);
    }

}
