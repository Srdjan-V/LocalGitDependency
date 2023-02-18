package srki2k.localgitdependency;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import srki2k.localgitdependency.depenency.DependencyManager;
import srki2k.localgitdependency.extentions.SettingsExtension;
import srki2k.localgitdependency.git.GitManager;
import srki2k.localgitdependency.gradle.GradleManager;
import srki2k.localgitdependency.persistence.PersistenceManager;
import srki2k.localgitdependency.property.PropertyManager;
import srki2k.localgitdependency.tasks.BuildGitDependencies;
import srki2k.localgitdependency.tasks.UndoLocalGitChanges;

public class LocalGitDependencyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java");

        Instances.setProject(project);// TODO: 18/02/2023 make multiProject compatible? 
        Instances.setDependencyManager(new DependencyManager());
        Instances.setGradleManager(new GradleManager());
        Instances.setPropertyManager(new PropertyManager());
        Instances.setGitManager(new GitManager());
        Instances.setPersistenceManager(new PersistenceManager());
        Instances.setSettingsExtension(project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, SettingsExtension.class));

        createTask(project, Constants.UNDO_LOCAL_GIT_CHANGES, UndoLocalGitChanges.class);
        createTask(project, Constants.BUILD_GIT_DEPENDENCIES, BuildGitDependencies.class);

        project.afterEvaluate(p -> {
            Instances.getGitManager().initRepos();
            Instances.getGradleManager().initGradleAPI();
            Instances.getPersistenceManager().savePersistentData();
            Instances.getGradleManager().buildDependencies();
            Instances.getDependencyManager().addBuiltDependencies();
        });
    }

    public static <T extends Task> T createTask(Project project, String name, Class<T> taskClass) {
        T task = project.getTasks().create(name, taskClass);
        task.setGroup(Constants.EXTENSION_NAME);
        return task;
    }
}
