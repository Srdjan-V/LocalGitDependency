package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.git.GitTasks;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;

public interface BaseUndoLocalChangesTask {
    default void clearChanges(GitManager gitManager, Dependency dependency) {
        if (gitManager.runRepoCommand(dependency, GitTasks::clearLocalChanges)) {
            if (dependency.getGitInfo().hasGitExceptions()) {
                dependency.getGitInfo().getGitExceptions().forEach(exception -> ManagerLogger.error(exception.getMessage()));
            } else {
                ManagerLogger.error("Unexpected error, gitManager reported git exceptions but gitInfo had non");
            }
        }
    }

}
