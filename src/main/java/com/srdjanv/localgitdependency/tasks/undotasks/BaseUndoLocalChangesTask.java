package com.srdjanv.localgitdependency.tasks.undotasks;

import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.git.GitTasks;

public interface BaseUndoLocalChangesTask {
    default void clearChanges(Dependency dependency) {
        if (Instances.getGitManager().runRepoCommand(dependency, GitTasks::clearLocalChanges)) {
            if (dependency.getGitInfo().hasGitExceptions()) {
                dependency.getGitInfo().getGitExceptions().forEach(exception -> Logger.error(exception.getMessage()));
            } else {
                Logger.error("Unexpected error, gitManager reported git exceptions but gitInfo had non");
            }
        }
    }

}
