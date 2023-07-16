package io.github.srdjanv.localgitdependency.tasks.startuptasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.gradle.IGradleManager;

interface BaseStartupTask {
    default void runStartupTasks(IGradleManager gradleManager, Dependency dependency) {
        gradleManager.startStartupTasks(dependency);
    }
}
