package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class BuildGitDependency extends BaseDynamicTask implements BaseBuildGitTask {

    @Inject
    public BuildGitDependency(Dependency dependency) {
        super(dependency);
        setDescription(String.format("This task will explicitly rebuild this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$BuildGitDependency() {
        buildGitDependency(dependency);
    }
}
