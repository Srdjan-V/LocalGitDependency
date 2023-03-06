package io.github.srdjanv.localgitdependency.tasks.buildtasks;

import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface BaseBuildGitTask {
    default void buildGitDependency(Dependency dependency) {
        Instances.getGradleManager().buildDependency(dependency);
    }
}
