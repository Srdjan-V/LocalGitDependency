package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class UndoLocalGitChanges extends BaseDynamicTask implements BaseUndoLocalChangesTask {

    @Inject
    public UndoLocalGitChanges(Dependency dependency) {
        super(dependency);
        setDescription(String.format("This task will undo local git changes to files for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$UndoLocalGitChanges() {
        clearChanges(dependency);
    }

}
