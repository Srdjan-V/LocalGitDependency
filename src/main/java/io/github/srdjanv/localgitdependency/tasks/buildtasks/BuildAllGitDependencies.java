package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import javax.inject.Inject;
import org.gradle.api.tasks.TaskAction;

public class BuildAllGitDependencies extends BaseProjectTask implements BaseBuildGitTask {

    @Inject
    public BuildAllGitDependencies(Managers managers) {
        super(managers);
        setDescription("This task will explicitly rebuild all dependencies");
    }

    @TaskAction
    public void task$BuildAllGitDependencies() {
        for (Dependency dependency : managers.getDependencyManager().getDependencies()) {
            buildGitDependency(managers.getGradleManager(), dependency);
        }
    }
}
