package com.srdjanv.localgitdependency.tasks.buildtasks;

import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.gradle.GradleManager;

public interface BaseBuildGitTask {
    default void buildGitDependency(GradleManager gradleManager, Dependency dependency) {
        gradleManager.buildDependency(dependency);
    }
}
