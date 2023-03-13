package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDynamicTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class PrintDependencyInfo extends BaseDynamicTask implements BasePrintInfoTask {

    @Inject
    public PrintDependencyInfo(Dependency dependency) {
        super(dependency);
        setDescription(String.format("This task will print general information for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$PrintDependencyInfo() throws IllegalAccessException {
        printInfo(dependency);
    }

}
