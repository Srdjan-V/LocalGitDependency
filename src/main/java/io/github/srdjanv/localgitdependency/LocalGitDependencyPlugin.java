package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyManagerInstance;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public final class LocalGitDependencyPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        project.getPluginManager().apply("java");
        project.getExtensions().add(LocalGitDependencyManagerInstance.class, Constants.LOCAL_GIT_DEPENDENCY_MANAGER_INSTANCE_EXTENSION, new LocalGitDependencyManagerInstance(project));
        project.afterEvaluate(p -> {
            if (p.getState().getFailure() != null) return;
            p.getExtensions().getByType(LocalGitDependencyManagerInstance.class).getProjectManager().startPlugin();
        });
    }

}
