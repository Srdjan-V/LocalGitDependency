package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Instances;
import org.gradle.api.tasks.TaskAction;

public abstract class BuildGitDependency extends BaseDependencyTask {
    @TaskAction
    public void task$BuildGitDependency() {
        Instances.getGradleManager().buildDependency(dependency);
    }

    @Override
    void createDescription() {
        setDescription(String.format("This task will explicitly rebuild this dependency: %s", dependency.getName()));
    }
}
