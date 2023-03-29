package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class ProbeAllDependenciesTask extends BaseProjectTask implements BaseProbeTask {
    @Inject
    public ProbeAllDependenciesTask(Managers managers) {
        super(managers);
        setDescription("This task will trigger probing for all dependencies");
    }

    @TaskAction
    public void task$ProbeAllDependenciesTask() {
        for (Dependency dependency : managers.getDependencyManager().getDependencies()) {
            probe(managers.getGitManager(), dependency);
        }
    }
}
