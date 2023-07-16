package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;

interface BaseBuildGitTask {
    default void buildGitDependency(IGradleManager gradleManager, Dependency dependency) {
        gradleManager.startBuildTasks(dependency);
    }
}
