package srki2k.localgitdependency.tasks;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.util.GitUtils;

import java.io.IOException;

public abstract class UndoLocalGitChanges extends DefaultTask {

    @TaskAction
    public void task$PullDependencies() {
        try {
            for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
                GitUtils.clearLocalChanges(dependency);
            }
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
