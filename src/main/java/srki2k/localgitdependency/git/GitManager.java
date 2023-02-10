package srki2k.localgitdependency.git;

import srki2k.localgitdependency.depenency.Dependency;

import java.util.function.Consumer;

public class GitManager {
    private GitManager() {
    }

    public static boolean initRepo(Dependency dependency) {
        try(GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency)) {
            gitObjectWrapper.setup();
            return gitObjectWrapper.hasGitExceptions();
        }
    }

    public static boolean runRepoCommand(Dependency dependency, Consumer<GitTasks> task) {
        try(GitObjectWrapper gitObjectWrapper = new GitObjectWrapper(dependency)) {
            task.accept(gitObjectWrapper);
            return gitObjectWrapper.hasGitExceptions();
        }
    }
}
