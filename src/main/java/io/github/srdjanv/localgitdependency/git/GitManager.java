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
        boolean expressions = false;
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (initRepo(dependency)) {
                expressions = true;
            }
        }
        if (expressions) {
            List<List<Exception>> exceptionList = new ArrayList<>();
            for (Dependency dependency : getDependencyManager().getDependencies()) {
                if (dependency.getGitInfo().hasGitExceptions())
                    exceptionList.add(dependency.getGitInfo().getGitExceptions());
            }
            RuntimeException runtimeException = new RuntimeException("Exception(s) occurred while interacting with git");
            exceptionList.stream().flatMap(List::stream).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
    }

    public boolean initRepo(Dependency dependency) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            gitObjectWrapper.setup();
            return gitObjectWrapper.hasGitExceptions();
        }
    }

    public boolean runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try (GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            task.accept(gitObjectWrapper);
            return gitObjectWrapper.hasGitExceptions();
        }
    }
}
