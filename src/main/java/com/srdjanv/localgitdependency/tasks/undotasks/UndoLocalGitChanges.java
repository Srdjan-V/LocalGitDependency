package com.srdjanv.localgitdependency.tasks.undotasks;

import com.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

public abstract class UndoLocalGitChanges extends BaseDynamicTask implements BaseUndoLocalChangesTask {
    @TaskAction
    public void task$UndoLocalGitChanges() {
        clearChanges(dependency);
    }

    @Override
    protected void createDescription() {
        setDescription(String.format("This task will undo local git changes to files for this dependency: %s", dependency.getName()));
    }
}
