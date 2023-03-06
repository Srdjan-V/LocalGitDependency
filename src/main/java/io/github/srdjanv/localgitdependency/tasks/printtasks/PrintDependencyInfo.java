package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

public abstract class PrintDependencyInfo extends BaseDynamicTask implements BasePrintInfoTask {

    @TaskAction
    public void task$PrintDependencyInfo() throws IllegalAccessException {
        printInfo(dependency);
    }

    @Override
    protected void createDescription() {
        setDescription(String.format("This task will print general information for this dependency: %s", dependency.getName()));
    }
}
