package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.extentions.LGDManagers;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public final class LocalGitDependencyPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        project.getPluginManager().apply("java");
        project.getExtensions().add(LGDManagers.class, LGDManagers.NAME, new LGDManagers(project));
        project.afterEvaluate(p -> {
            if (p.getState().getFailure() != null) return;
            p.getExtensions().getByType(LGDManagers.class).getProjectManager().startPlugin();
        });
    }
}
