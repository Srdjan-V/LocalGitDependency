package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Instances;
import org.gradle.api.tasks.TaskAction;

public abstract class BuildGitDependency extends BaseDependencyTask {
    @TaskAction
    public void task$BuildGitDependency() {
        Instances.getGradleManager().buildDependency(dependency);
    }

}
