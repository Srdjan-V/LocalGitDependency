package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public class BuildAllGitDependencies extends BaseSingleTask implements BaseBuildGitTask {

    @TaskAction
    public void task$BuildAllGitDependencies() {
        Instances.getDependencyManager().getDependencies().forEach(this::buildGitDependency);
    }

    @Override
    protected void createDescription() {
        setDescription("This task will explicitly rebuild all dependencies");
    }
}
