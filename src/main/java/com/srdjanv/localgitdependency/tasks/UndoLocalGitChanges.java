package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.git.GitTasks;
import org.gradle.api.tasks.TaskAction;
import com.srdjanv.localgitdependency.Instances;

public abstract class UndoLocalGitChanges extends BaseDependencyTask {
    @TaskAction
    public void task$UndoLocalGitChanges() {
        if (Instances.getGitManager().runRepoCommand(dependency, GitTasks::clearLocalChanges)) {
            if (dependency.getGitInfo().hasGitExceptions()) {
                dependency.getGitInfo().getGitExceptions().forEach(exception -> Logger.error(exception.getMessage()));
            } else {
                Logger.error("Unexpected error, gitManager reported git exceptions but gitInfo had non");
            }
        }
    }

    @Override
    void createDescription() {
        setDescription(String.format("This task will undo local git changes to files for this dependency: %s", dependency.getName()));
    }
}
