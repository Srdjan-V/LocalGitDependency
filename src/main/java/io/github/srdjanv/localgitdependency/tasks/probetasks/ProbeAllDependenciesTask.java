package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class ProbeAllDependenciesTask extends BaseProjectTask implements BaseProbeTask {
    @Inject
    public ProbeAllDependenciesTask(ProjectInstances projectInstances) {
        super(projectInstances);
        setDescription("This task will trigger probing for all dependencies");
    }

    @TaskAction
    public void task$ProbeAllDependenciesTask() {
        for (Dependency dependency : projectInstances.getDependencyManager().getDependencies()) {
            probe(projectInstances.getGitManager(), dependency);
        }
    }
}
