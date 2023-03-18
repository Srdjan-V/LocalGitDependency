package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GitManager extends ManagerBase {
    public GitManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {

    }

    public void initRepos() {
        List<List<Exception>> gitExceptions = null;
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            GitReport gitReport = initRepo(dependency);

            if (gitReport.isHasGitExceptions()) {
                if (gitExceptions == null) {
                    gitExceptions = new ArrayList<>();
                }
                gitExceptions.add(gitReport.getGitExceptions());
            }
        }
        if (gitExceptions != null) {
            RuntimeException runtimeException = new RuntimeException("Exception(s) occurred while interacting with git");
            gitExceptions.stream().flatMap(List::stream).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
    }

    public GitReport initRepo(Dependency dependency) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            gitObjectWrapper.setup();
            return gitObjectWrapper.getGitReport();
        }
    }

    public GitReport runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            task.accept(gitObjectWrapper);
            return gitObjectWrapper.getGitReport();
        }
    }

    public static class GitReport {
        private final List<Exception> gitExceptions;
        private final boolean hasGitExceptions;

        public GitReport(List<Exception> gitExceptions) {
            this.gitExceptions = gitExceptions;
            this.hasGitExceptions = gitExceptions != null;
        }

        public List<Exception> getGitExceptions() {
            return gitExceptions;
        }

        public boolean isHasGitExceptions() {
            return hasGitExceptions;
        }
    }

}
