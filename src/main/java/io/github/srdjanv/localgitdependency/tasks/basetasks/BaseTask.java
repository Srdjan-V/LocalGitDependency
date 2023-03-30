package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.DefaultTask;

abstract class BaseTask extends DefaultTask {
    protected final Managers managers;

    public BaseTask(Managers managers) {
        this.managers = managers;
    }
}
