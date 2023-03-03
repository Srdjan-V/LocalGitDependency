package com.srdjanv.localgitdependency.tasks.buildtasks;

import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public abstract class BuildAllGitDependencies extends BaseSingleTask implements BaseBuildGitTask {

    @TaskAction
    public void task$BuildAllGitDependencies() {
        Instances.getDependencyManager().getDependencies().forEach(this::buildGitDependency);
    }

    @Override
    protected void createDescription() {
        setDescription("This task will explicitly rebuild all dependencies");
    }
}
