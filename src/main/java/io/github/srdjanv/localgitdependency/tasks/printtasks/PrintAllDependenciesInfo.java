package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.tasks.basetasks.BaseProjectTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class PrintAllDependenciesInfo extends BaseProjectTask implements BasePrintInfoTask {

    @Inject
    public PrintAllDependenciesInfo(ProjectInstances projectInstances) {
        super(projectInstances);
        setDescription("This task will print general information for all dependencies");
    }

    @TaskAction
    public void task$PrintAllDependenciesInfo() throws IllegalAccessException {
        for (Dependency dependency : projectInstances.getDependencyManager().getDependencies()) {
            printInfo(dependency);
        }
    }

}
