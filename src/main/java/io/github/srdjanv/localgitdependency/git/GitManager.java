package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class GitManager extends ManagerBase implements IGitManager {
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
            throw new GitException(gitExceptions);
        }
    }

    @Override
    public GitReport initRepo(Dependency dependency) {
        try (GitWrapper gitWrapper = new GitWrapper(dependency.getGitInfo())) {
            gitWrapper.setup();
            return gitWrapper.getGitReport();
        }
    }

    @Override
    public GitReport runRepoCommand(Dependency dependency, Consumer<IGitTasks> task) {
        try (IntractableGitWrapper gitWrapper = new IntractableGitWrapper(dependency.getGitInfo())) {
            task.accept(gitWrapper);
            return gitWrapper.getGitReport();
        }
    }
}
