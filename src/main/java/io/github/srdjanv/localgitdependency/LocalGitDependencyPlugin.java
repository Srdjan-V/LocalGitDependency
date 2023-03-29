package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.project.IProjectManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalGitDependencyPlugin implements Plugin<Project> {
    private static final Map<File, IProjectManager> projectRegistry = new HashMap<>();

    public static IProjectManager getProject(Project project) {
        return projectRegistry.get(project.getProjectDir());
    }

    @Override
    public void apply(@NotNull Project project) {
        IProjectManager createdProjectManager = IProjectManager.createProject(project);
        projectRegistry.put(project.getProjectDir(), createdProjectManager);

        project.afterEvaluate(p -> getProject(p).startPlugin());
    }

}
