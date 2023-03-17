package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class BuildAllGitDependencies extends BaseProjectTask implements BaseBuildGitTask {

    @Inject
    public BuildAllGitDependencies(ProjectInstances projectInstances) {
        super(projectInstances);
        setDescription("This task will explicitly rebuild all dependencies");
    }

    @TaskAction
    public void task$BuildAllGitDependencies() {
        for (Dependency dependency : projectInstances.getDependencyManager().getDependencies()) {
            buildGitDependency(projectInstances.getGradleManager(), dependency);
        }
    }
}
