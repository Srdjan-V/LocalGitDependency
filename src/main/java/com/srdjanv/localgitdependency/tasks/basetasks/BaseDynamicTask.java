package com.srdjanv.localgitdependency.tasks.basetasks;

import com.srdjanv.localgitdependency.depenency.Dependency;

public abstract class BaseDynamicTask extends BaseTask {
    protected Dependency dependency;

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
        createDescription();
    }

}
