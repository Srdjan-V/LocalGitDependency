package io.github.srdjanv.localgitdependency.tasks.undotasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import javax.inject.Inject;
import org.gradle.api.tasks.TaskAction;

public class UndoAllLocalGitChanges extends BaseProjectTask implements BaseUndoLocalChangesTask {
    @Inject
    public UndoAllLocalGitChanges(Managers managers) {
        super(managers);
        setDescription("This task will undo local git changes to files for all dependencies");
    }

    @TaskAction
    public void task$UndoLocalGitChanges() {
        for (Dependency dependency : managers.getDependencyManager().getDependencies()) {
            clearChanges(managers.getGitManager(), dependency);
        }
    }
}
