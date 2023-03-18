package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.cleanup.CleanupManager;
import io.github.srdjanv.localgitdependency.depenency.DependencyManager;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyExtension;
import io.github.srdjanv.localgitdependency.git.GitManager;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;
import io.github.srdjanv.localgitdependency.persistence.PersistenceManager;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import io.github.srdjanv.localgitdependency.tasks.TasksManager;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectInstances implements Managers {
    private final Project project;
    private final ProjectManager projectManager;
    private final PropertyManager propertyManager;
    private final DependencyManager dependencyManager;
    private final GitManager gitManager;
    private final GradleManager gradleManager;
    private final PersistenceManager persistenceManager;
    private final TasksManager tasksManager;
    private final LocalGitDependencyExtension localGitDependencyExtension;
    private final CleanupManager cleanupManager;

    public ProjectInstances(Project project) {
        this.project = project;

        final List<ManagerBase> managerList = new ArrayList<>();
        managerList.add(projectManager = new ProjectManager(this));
        managerList.add(localGitDependencyExtension = project.getExtensions().create(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION, LocalGitDependencyExtension.class, this));
        managerList.add(dependencyManager = new DependencyManager(this));
        managerList.add(propertyManager = new PropertyManager(this));
        managerList.add(gitManager = new GitManager(this));
        managerList.add(gradleManager = new GradleManager(this));
        managerList.add(persistenceManager = new PersistenceManager(this));
        managerList.add(tasksManager = new TasksManager(this));
        managerList.add(cleanupManager = new CleanupManager(this));

        for (ManagerBase managerBase : managerList) {
            managerBase.managerConstructor();
        }
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public ProjectManager getProjectManager() {
        return projectManager;
    }

    @Override
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public GitManager getGitManager() {
        return gitManager;
    }

    @Override
    public GradleManager getGradleManager() {
        return gradleManager;
    }

    @Override
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    @Override
    public TasksManager getTasksManager() {
        return tasksManager;
    }

    @Override
    public LocalGitDependencyExtension getLocalGitDependencyExtension() {
        return localGitDependencyExtension;
    }

    @Override
    public CleanupManager getCleanupManager() {
        return cleanupManager;
    }
}
