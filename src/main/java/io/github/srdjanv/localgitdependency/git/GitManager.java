package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
            throw new GitException(gitExceptions.stream().flatMap(List::stream).collect(Collectors.toList()));
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
    public GitReport runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try (IntractableGitWrapper gitWrapper = new IntractableGitWrapper(dependency.getGitInfo())) {
            task.accept(gitWrapper);
            return gitWrapper.getGitReport();
        }
    }
}
