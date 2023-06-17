package io.github.srdjanv.localgitdependency.tasks.startuptasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class RunAllStartupTasks extends BaseProjectTask implements BaseStartupTask {

    @Inject
    public RunAllStartupTasks(Managers managers) {
        super(managers);
        setDescription("This task will explicitly rerun startup tasks for all dependencies");
    }

    @TaskAction
    public void task$BuildAllGitDependencies() {
        for (Dependency dependency : managers.getDependencyManager().getDependencies()) {
            runStartupTasks(managers.getGradleManager(), dependency);
        }
    }
}
