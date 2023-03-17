package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.gradle.GradleManager;

public interface BaseBuildGitTask {
    default void buildGitDependency(GradleManager gradleManager,Dependency dependency) {
        gradleManager.buildDependency(dependency);
    }
}
