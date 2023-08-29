package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IGradleManager extends Manager {
    static IGradleManager createInstance(Managers managers) {
        return new GradleManager(managers);
    }

    @TaskDescription("setting up gradle files")
    void initGradleAPI();

    @TaskDescription("building dependencies")
    void startBuildTasks();

    void startStartupTasks(Dependency dependency);

    void startProbeTasks(Dependency dependency);

    void startBuildTasks(Dependency dependency);
}
