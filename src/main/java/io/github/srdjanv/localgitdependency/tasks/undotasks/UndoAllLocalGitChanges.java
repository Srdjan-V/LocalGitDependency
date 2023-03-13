package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public class UndoAllLocalGitChanges extends BaseSingleTask implements BaseUndoLocalChangesTask {

    public UndoAllLocalGitChanges() {
        setDescription("This task will undo local git changes to files for all dependencies");
    }

    @TaskAction
    public void task$UndoLocalGitChanges() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            clearChanges(dependency);
        }
    }

}
