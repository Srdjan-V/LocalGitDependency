package com.srdjanv.localgitdependency.persistence;

import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.Instances;

public class PersistenceManager {

    // TODO: 02/03/2023 serialize this field 
    private String initScriptSHA;

    public String getInitScriptSHA() {
        return initScriptSHA;
    }

    public void setInitScriptSHA(String initScriptSHA) {
        this.initScriptSHA = initScriptSHA;
    }

    public void savePersistentData() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            dependency.getPersistentInfo().saveToPersistentFile();
        }
    }

}
