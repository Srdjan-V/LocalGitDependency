package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public interface IPersistenceManager extends Managers {
    static IPersistenceManager createInstance(ProjectInstances projectInstances){
        return new PersistenceManager(projectInstances);
    }
    String getInitScriptSHA();
    void setInitScriptSHA(String initScriptSHA);
    void loadPersistentData();
    void savePersistentData();
    void loadProjectPersistentData();
    void saveProjectPersistentData();
    void loadDependencyPersistentData(Dependency dependency);
    void saveDependencyPersistentData(Dependency dependency);
}
