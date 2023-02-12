package srki2k.localgitdependency;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import srki2k.localgitdependency.depenency.DependencyManager;
import srki2k.localgitdependency.extentions.SettingsExtension;
import srki2k.localgitdependency.git.GitManager;
import srki2k.localgitdependency.property.PropertyManager;
import srki2k.localgitdependency.tasks.BuildGitDependencies;
import srki2k.localgitdependency.tasks.UndoLocalGitChanges;
import srki2k.localgitdependency.gradle.GradleManager;

public class GitProjectPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java");
        project.getRepositories().add(project.getRepositories().mavenLocal());

        Instances.setProject(project);
        Instances.setDependencyManager(new DependencyManager());
        Instances.setGradleApiManager(new GradleManager());
        Instances.setPropertyManager(new PropertyManager());
        Instances.setGitManager(new GitManager());
        Instances.setSettingsExtension(project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, SettingsExtension.class));

        createTask(project, Constants.UNDO_LOCAL_GIT_CHANGES, UndoLocalGitChanges.class);
        createTask(project, Constants.BUILD_GIT_DEPENDENCIES, BuildGitDependencies.class);

        project.afterEvaluate(p -> {
            Instances.getGitManager().initRepos();
            Instances.getGradleApiManager().initGradleAPI();
            Instances.getDependencyManager().buildDependencies(false);
            Instances.getDependencyManager().addBuiltDependencies();
        });
    }

    public static <T extends Task> T createTask(Project project, String name, Class<T> taskClass) {
        T task = project.getTasks().create(name, taskClass);
        task.setGroup(Constants.EXTENSION_NAME);
        return task;
    }
}
