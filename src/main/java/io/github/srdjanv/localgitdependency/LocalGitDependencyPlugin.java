package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.extentions.LGDManagers;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

public final class LocalGitDependencyPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        if (GradleVersion.current().compareTo(GradleVersion.version("5.1")) < 0)
            throw new GradleException("Unsupported gradle version, lgd only supports 5.1 and up");

        project.getPluginManager().apply("java");
        project.getExtensions().add(LGDManagers.class, LGDManagers.NAME, new LGDManagers(project));
        project.afterEvaluate(p -> {
            if (p.getState().getFailure() != null) return;
            p.getExtensions().getByType(LGDManagers.class).getProjectManager().startPlugin();
        });
    }
}
