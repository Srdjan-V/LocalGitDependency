package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.Logger;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitTasks;

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
