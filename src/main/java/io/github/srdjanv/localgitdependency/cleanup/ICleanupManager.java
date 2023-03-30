package io.github.srdjanv.localgitdependency.cleanup;

import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface ICleanupManager extends Manager {
    static ICleanupManager createInstance(Managers projectInstances){
        return new CleanupManager(projectInstances);
    }
    @TaskDescription("cleaning directories")
    void init();
}
