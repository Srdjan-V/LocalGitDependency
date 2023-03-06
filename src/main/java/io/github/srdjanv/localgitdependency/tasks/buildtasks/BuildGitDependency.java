package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

public abstract class BuildGitDependency extends BaseDynamicTask implements BaseBuildGitTask {
    @TaskAction
    public void task$BuildGitDependency() {
        buildGitDependency(dependency);
    }

    @Override
    protected void createDescription() {
        setDescription(String.format("This task will explicitly rebuild this dependency: %s", dependency.getName()));
    }
}
