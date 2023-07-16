package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class RunBuildTasks extends BaseDependencyTask implements BaseBuildGitTask {

    @Inject
    public RunBuildTasks(Managers managers, Dependency dependency) {
        super(managers, dependency);
        setDescription(String.format("This task will explicitly rebuild this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$BuildGitDependency() {
        buildGitDependency(managers.getGradleManager(), dependency);
    }
}
