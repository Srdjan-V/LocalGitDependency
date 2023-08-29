package io.github.srdjanv.localgitdependency.tasks.startuptasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import javax.inject.Inject;
import org.gradle.api.tasks.TaskAction;

public class RunStartupTasks extends BaseDependencyTask implements BaseStartupTask {

    @Inject
    public RunStartupTasks(Managers managers, Dependency dependency) {
        super(managers, dependency);
        setDescription(String.format(
                "This task will explicitly rerun startup tasks for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$RunStartupTasks() {
        runStartupTasks(managers.getGradleManager(), dependency);
    }
}
