package io.github.srdjanv.localgitdependency.ideintegration;

import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IIDEManager {
    static IIDEManager createInstance(Managers managers) {
        return new IDEManager(managers);
    }
    @TaskDescription("handling SourceSets")
    boolean handelSourceSets();
}
