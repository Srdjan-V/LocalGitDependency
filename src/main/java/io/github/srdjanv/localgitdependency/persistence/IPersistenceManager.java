package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.project.ProjectInstances;

public interface IPersistenceManager {
    static IPersistenceManager createInstance(ProjectInstances projectInstances){
        return new PersistenceManager(projectInstances);
    }
    String getInitScriptSHA();
    void setInitScriptSHA(String initScriptSHA);
    void savePersistentData();
}
