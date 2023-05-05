package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyManagerInstance;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class LocalGitDependencyPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        project.getExtensions().add(LocalGitDependencyManagerInstance.class, "LocalGitDependencyManagerInstance", new LocalGitDependencyManagerInstance(project));
        project.afterEvaluate(p -> p.getExtensions().getByType(LocalGitDependencyManagerInstance.class).getProjectManager().startPlugin());
    }

}
