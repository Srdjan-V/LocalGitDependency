package io.github.srdjanv.localgitdependency.tasks.basetasks;

import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import org.gradle.api.DefaultTask;

public class BaseTask extends DefaultTask {
    protected final ProjectInstances projectInstances;

    public BaseTask(ProjectInstances projectInstances) {
        this.projectInstances = projectInstances;
    }
}
