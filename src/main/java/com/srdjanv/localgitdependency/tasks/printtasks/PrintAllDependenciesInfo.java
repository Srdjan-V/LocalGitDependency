package com.srdjanv.localgitdependency.tasks.printtasks;

import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public abstract class PrintAllDependenciesInfo extends BaseSingleTask implements BasePrintInfoTask {

    @TaskAction
    public void task$PrintAllDependenciesInfo() throws IllegalAccessException {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            printInfo(dependency);
        }
    }

    @Override
    protected void createDescription() {
        setDescription("This task will print general information for all dependencies");
    }
}
