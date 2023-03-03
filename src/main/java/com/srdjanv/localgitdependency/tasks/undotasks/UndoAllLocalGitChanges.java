package com.srdjanv.localgitdependency.tasks.undotasks;

import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public class UndoAllLocalGitChanges extends BaseSingleTask implements BaseUndoLocalChangesTask {
    @TaskAction
    public void task$UndoLocalGitChanges() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            clearChanges(dependency);
        }
    }

    @Override
    protected void createDescription() {
        setDescription("This task will undo local git changes to files for all dependencies");
    }

}
