package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import java.io.IOException;
import java.util.Optional;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.TagOpt;

final class IntractableGitWrapper implements IGitTasks, AutoCloseable {
    private final GitWrapper gitWrapper;

    IntractableGitWrapper(GitInfo gitInfo) {
        gitWrapper = new GitWrapper(gitInfo);
    }

    @Override
    public void setup() {
        gitWrapper.setup();
    }

    @Override
    public Optional<Boolean> hasLocalChanges() {
        if (gitWrapper.hasGitExceptions()) return Optional.empty();
        try {
            return Optional.ofNullable(gitWrapper.hasLocalChanges());
        } catch (GitAPIException | IOException e) {
            ManagerLogger.error("Unable to check for local changes", e);
            gitWrapper.addGitExceptions(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> isUpToDateWithRemote() {
        if (gitWrapper.hasGitExceptions()) return Optional.empty();
        try {
            return Optional.of(gitWrapper.isUpToDateWithRemote());
        } catch (GitAPIException | IOException e) {
            ManagerLogger.error("Unable to check if it up to date with remote", e);
            gitWrapper.addGitExceptions(e);
            return Optional.empty();
        }
    }

    @Override
    public void clearLocalChanges() {
        if (gitWrapper.hasGitExceptions()) return;
        ManagerLogger.info(
                "Dependency {}, clearing local changes and marking dependency to be rebuild",
                gitWrapper.gitInfo.getDependency().getName());
        try {
            if (Boolean.TRUE.equals(gitWrapper.hasLocalChanges())) {
                gitWrapper.git.reset().setMode(ResetCommand.ResetType.HARD).call();
                gitWrapper.gitInfo.setRefreshed();
            }
        } catch (IOException | GitAPIException e) {
            ManagerLogger.error("Unable to clear local changes", e);
            gitWrapper.addGitExceptions(e);
        }
    }

    @Override
    public void update() {
        if (gitWrapper.hasGitExceptions()) return;
        try {
            gitWrapper.git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
            gitWrapper.update();
        } catch (GitAPIException | IOException e) {
            ManagerLogger.error("Error updating dependency", e);
            gitWrapper.addGitExceptions(e);
        }
    }

    public GitReport getGitReport() {
        return gitWrapper.getGitReport();
    }

    @Override
    public void close() {
        gitWrapper.close();
    }
}
