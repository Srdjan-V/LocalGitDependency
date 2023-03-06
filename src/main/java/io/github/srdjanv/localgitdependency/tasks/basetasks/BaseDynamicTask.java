package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

public abstract class BaseDynamicTask extends BaseTask {
    protected Dependency dependency;

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
        createDescription();
    }

}
