package srki2k.localgitdependency.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import srki2k.localgitdependency.Instances;

public abstract class BuildGitDependencies extends DefaultTask {

    @TaskAction
    public void task$BuildGitDependencies() {
        Instances.getDependencyManager().buildDependencies(true);
    }

}
