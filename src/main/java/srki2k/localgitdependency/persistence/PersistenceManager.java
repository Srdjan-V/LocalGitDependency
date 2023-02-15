package srki2k.localgitdependency.persistence;

import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;

public class PersistenceManager {
    public void savePersistentData() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            dependency.getPersistentInfo().saveToPersistentFile();
        }
    }

}
