package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;

public abstract class BaseDependencyTask extends BaseTask {
    protected Dependency dependency;

    public BaseDependencyTask(Managers managers, Dependency dependency) {
        super(managers);
        this.dependency = dependency;
    }
}
