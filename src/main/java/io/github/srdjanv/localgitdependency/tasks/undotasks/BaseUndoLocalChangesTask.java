package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitReport;
import io.github.srdjanv.localgitdependency.git.IGitManager;
import io.github.srdjanv.localgitdependency.git.IGitTasks;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;

interface BaseUndoLocalChangesTask {
    default void clearChanges(IGitManager gitManager, Dependency dependency) {
        GitReport gitReport = gitManager.runRepoCommand(dependency, IGitTasks::clearLocalChanges);

        if (gitReport.hasGitExceptions()) {
            gitReport.getGitExceptions().forEach(exception -> ManagerLogger.error(exception.getMessage()));
        }
    }
}
