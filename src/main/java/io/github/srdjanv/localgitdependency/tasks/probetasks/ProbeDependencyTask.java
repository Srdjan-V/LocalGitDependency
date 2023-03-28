package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

public class ProbeDependencyTask extends BaseDependencyTask implements BaseProbeTask {
    public ProbeDependencyTask(ProjectInstances projectInstances, Dependency dependency) {
        super(projectInstances, dependency);
    }

    @TaskAction
    public void task$ProbeDependencyTask() {
        probe(projectInstances.getGitManager(), dependency);
    }
}
