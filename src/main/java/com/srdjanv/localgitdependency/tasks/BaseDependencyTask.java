package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.DefaultTask;

abstract class BaseDependencyTask extends DefaultTask {
    Dependency dependency;

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    abstract void createDescription();

}
