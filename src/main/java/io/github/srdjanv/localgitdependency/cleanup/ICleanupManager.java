package io.github.srdjanv.localgitdependency.cleanup;

import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public interface ICleanupManager extends Managers {
    static ICleanupManager createInstance(ProjectInstances projectInstances){
        return new CleanupManager(projectInstances);
    }
    void init();
}
