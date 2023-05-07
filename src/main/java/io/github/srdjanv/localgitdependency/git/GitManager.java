package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class GitManager extends ManagerBase implements IGitManager {
    GitManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void initRepos() {
        List<List<Exception>> gitExceptions = null;
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            GitReport gitReport = initRepo(dependency);

            if (gitReport.hasGitExceptions()) {
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

    @Override
    public GitReport initRepo(Dependency dependency) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            gitObjectWrapper.setup();
            return gitObjectWrapper.getGitReport();
        }
    }

    @Override
    public GitReport runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            task.accept(gitObjectWrapper);
            return gitObjectWrapper.getGitReport();
        }
    }

}
