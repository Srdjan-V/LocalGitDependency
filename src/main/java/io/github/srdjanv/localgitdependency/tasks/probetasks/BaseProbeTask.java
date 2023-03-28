package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;

interface BaseProbeTask {
    default void probe(Managers managers, Dependency dependency) {
        managers.getGradleManager().probeProject(dependency);
        dependency.getPersistentInfo().saveToPersistentFile();
    }

}
