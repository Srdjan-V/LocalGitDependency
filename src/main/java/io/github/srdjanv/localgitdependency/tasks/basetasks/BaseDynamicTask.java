package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.DefaultTask;

public abstract class BaseDynamicTask extends DefaultTask {
    protected Dependency dependency;

    public BaseDynamicTask(Dependency dependency) {
        this.dependency = dependency;
    }
}
