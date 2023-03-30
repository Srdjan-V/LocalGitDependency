package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class UndoLocalGitChanges extends BaseDependencyTask implements BaseUndoLocalChangesTask {
    @Inject
    public UndoLocalGitChanges(Managers managers, Dependency dependency) {
        super(managers, dependency);
        setDescription(String.format("This task will undo local git changes to files for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$UndoLocalGitChanges() {
        clearChanges(managers.getGitManager(), dependency);
    }

}
