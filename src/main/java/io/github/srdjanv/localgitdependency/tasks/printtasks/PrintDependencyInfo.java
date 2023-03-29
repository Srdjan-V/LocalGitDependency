package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseDependencyTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class PrintDependencyInfo extends BaseDependencyTask implements BasePrintInfoTask {

    @Inject
    public PrintDependencyInfo(Managers managers, Dependency dependency) {
        super(managers, dependency);
        setDescription(String.format("This task will print general information for this dependency: %s", dependency.getName()));
    }

    @TaskAction
    public void task$PrintDependencyInfo() throws IllegalAccessException {
        printInfo(dependency);
    }

}
