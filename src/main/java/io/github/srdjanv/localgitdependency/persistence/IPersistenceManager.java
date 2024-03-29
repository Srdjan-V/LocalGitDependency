package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

public interface IPersistenceManager extends Manager {
    static IPersistenceManager createInstance(Managers managers){
        return new PersistenceManager(managers);
    }
    String getInitScriptSHA();
    void setInitScriptSHA(String initScriptSHA);
    @TaskDescription("loading persistent data")
    void loadPersistentData();
    @TaskDescription("saving persistent data")
    void savePersistentData();
    void loadProjectPersistentData();
    void saveProjectPersistentData();
    void loadDependencyPersistentData(Dependency dependency);
    void saveDependencyPersistentData(Dependency dependency);
}
