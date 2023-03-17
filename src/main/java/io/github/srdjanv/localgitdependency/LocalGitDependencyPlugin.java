package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.project.ProjectManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalGitDependencyPlugin implements Plugin<Project> {
    private static final Map<File, ProjectManager> projectRegistry = new HashMap<>();

    public static ProjectManager getProject(Project project) {
        return projectRegistry.get(project.getProjectDir());
    }

    @Override
    public void apply(@NotNull Project project) {
        ProjectManager createdProjectManager = ProjectManager.createProject(project);
        projectRegistry.put(project.getProjectDir(), createdProjectManager);

        project.afterEvaluate(p -> projectRegistry.get(p.getProjectDir()).startPlugin());
    }

}
