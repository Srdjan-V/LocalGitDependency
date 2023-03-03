package com.srdjanv.localgitdependency.tasks.buildtasks;

import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;

public interface BaseBuildGitTask {
    default void buildGitDependency(Dependency dependency) {
        Instances.getGradleManager().buildDependency(dependency);
    }
}
