package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class ProbeDependencyTask extends BaseDependencyTask implements BaseProbeTask {
    @Inject
    public ProbeDependencyTask(ProjectInstances projectInstances, Dependency dependency) {
        super(projectInstances, dependency);
        setDescription(String.format("This task will trigger probing for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$ProbeDependencyTask() {
        probe(projectInstances.getGitManager(), dependency);
    }
}
