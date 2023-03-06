package io.github.srdjanv.localgitdependency.tasks.basetasks;

import org.gradle.api.DefaultTask;

abstract class BaseTask extends DefaultTask {
    protected abstract void createDescription();
}
