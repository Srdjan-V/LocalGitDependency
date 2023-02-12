package srki2k.localgitdependency.git;

import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GitManager {
    public void initRepos() {
        boolean expressions = false;
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (initRepo(dependency)) {
                expressions = true;
            }
        }
        if (expressions) {
            List<List<Exception>> exceptionList = new ArrayList<>();
            for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
                exceptionList.add(dependency.getGitInfo().getGitExceptions());
            }
            RuntimeException runtimeException = new RuntimeException("Exception(s) occurred while interacting with git");
            exceptionList.stream().flatMap(List::stream).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
    }

    public boolean initRepo(Dependency dependency) {
        try(GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            gitObjectWrapper.setup();
            return gitObjectWrapper.hasGitExceptions();
        }
    }

    public boolean runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try(GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency.getGitInfo())) {
            task.accept(gitObjectWrapper);
            return gitObjectWrapper.hasGitExceptions();
        }
    }
}
