package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class BuildGitDependency extends BaseDependencyTask implements BaseBuildGitTask {

    @Inject
    public BuildGitDependency(ProjectInstances projectInstances, Dependency dependency) {
        super(projectInstances, dependency);
        setDescription(String.format("This task will explicitly rebuild this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$BuildGitDependency() {
        buildGitDependency(projectInstances.getGradleManager(), dependency);
    }
}
