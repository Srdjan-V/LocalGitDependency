package com.srdjanv.localgitdependency.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public abstract class BuildGitDependencies extends DefaultTask {

    @TaskAction
    public void task$BuildGitDependencies() {
        // TODO: 13/02/2023 probably remove
        //Instances.getDependencyManager().buildDependencies(true);
    }

}
