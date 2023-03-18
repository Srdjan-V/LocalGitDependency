package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public abstract class BaseDependencyTask extends BaseTask {
    protected Dependency dependency;

    public BaseDependencyTask(ProjectInstances projectInstances, Dependency dependency) {
        super(projectInstances);
        this.dependency = dependency;
    }
}
