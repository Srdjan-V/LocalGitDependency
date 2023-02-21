package com.srdjanv.localgitdependency;

import com.srdjanv.localgitdependency.depenency.DependencyManager;
import com.srdjanv.localgitdependency.extentions.SettingsExtension;
import com.srdjanv.localgitdependency.git.GitManager;
import com.srdjanv.localgitdependency.gradle.GradleManager;
import com.srdjanv.localgitdependency.persistence.PersistenceManager;
import com.srdjanv.localgitdependency.property.PropertyManager;
import com.srdjanv.localgitdependency.tasks.BuildGitDependencies;
import com.srdjanv.localgitdependency.tasks.UndoLocalGitChanges;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

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
