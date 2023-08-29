package io.github.srdjanv.localgitdependency.tasks.probetasks;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Manager;

interface BaseProbeTask {
    default void probe(Manager manager, Dependency dependency) {
        manager.getGradleManager().startProbeTasks(dependency);
        manager.getPersistenceManager().saveDependencyPersistentData(dependency);
    }
}
