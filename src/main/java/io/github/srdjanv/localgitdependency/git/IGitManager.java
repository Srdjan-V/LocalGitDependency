package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IGitManager extends Manager {
    static IGitManager createInstance(Managers managers) {
        return new GitManager(managers);
    }

    @TaskDescription("setting up repos")
    boolean initRepos();

    boolean initRepo(Dependency dependency);
}
