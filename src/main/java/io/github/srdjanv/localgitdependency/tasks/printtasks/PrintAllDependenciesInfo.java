package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseSingleTask;
import org.gradle.api.tasks.TaskAction;

public class PrintAllDependenciesInfo extends BaseSingleTask implements BasePrintInfoTask {

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
