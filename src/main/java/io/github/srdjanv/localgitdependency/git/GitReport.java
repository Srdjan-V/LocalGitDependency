package io.github.srdjanv.localgitdependency.git;

import java.util.List;

public final class GitReport {
    private final List<Exception> gitExceptions;
    private final boolean hasGitExceptions;

    GitReport(List<Exception> gitExceptions) {
        this.gitExceptions = gitExceptions;
        this.hasGitExceptions = gitExceptions != null;
    }

    public List<Exception> getGitExceptions() {
        return gitExceptions;
    }

    public boolean hasGitExceptions() {
        return hasGitExceptions;
    }
}
