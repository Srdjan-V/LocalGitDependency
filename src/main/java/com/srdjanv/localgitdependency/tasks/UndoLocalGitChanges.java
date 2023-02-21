package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.git.GitTasks;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import com.srdjanv.localgitdependency.Instances;

public abstract class UndoLocalGitChanges extends DefaultTask {

    @TaskAction
    public void task$PullDependencies() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            Instances.getGitManager().runRepoCommand(dependency, GitTasks::clearLocalChanges);
        }
    }
}
