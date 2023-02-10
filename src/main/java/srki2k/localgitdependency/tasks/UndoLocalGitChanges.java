package srki2k.localgitdependency.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.git.GitManager;
import srki2k.localgitdependency.git.GitTasks;

public abstract class UndoLocalGitChanges extends DefaultTask {

    @TaskAction
    public void task$PullDependencies() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            GitManager.runRepoCommand(dependency, GitTasks::clearLocalChanges);
        }
    }
}
