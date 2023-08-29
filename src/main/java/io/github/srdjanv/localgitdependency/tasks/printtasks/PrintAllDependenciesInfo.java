package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import javax.inject.Inject;
import org.gradle.api.tasks.TaskAction;

public class PrintAllDependenciesInfo extends BaseProjectTask implements BasePrintInfoTask {

    @Inject
    public PrintAllDependenciesInfo(Managers managers) {
        super(managers);
        setDescription("This task will print general information for all dependencies");
    }

    @TaskAction
    public void task$PrintAllDependenciesInfo() throws IllegalAccessException {
        for (Dependency dependency : managers.getDependencyManager().getDependencies()) {
            printInfo(dependency);
        }
    }
}
