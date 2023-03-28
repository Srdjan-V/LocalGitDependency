package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

import java.util.function.Consumer;

public interface IGitManager extends Managers {
    static IGitManager createInstance(ProjectInstances projectInstances){
        return new GitManager(projectInstances);
    }
    void initRepos();
    GitReport initRepo(Dependency dependency);
    GitReport runRepoCommand(Dependency dependency, Consumer<GitTasks> task);
}
