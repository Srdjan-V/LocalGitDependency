package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

//Base property's for dependency's global configurations
public abstract class CommonProperty extends CommonPropertyFields {
    CommonProperty() {
    }

    public void defaultConfiguration(String defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void manualBuild(boolean manualBuild) {
        this.manualBuild = manualBuild;
    }

    public void keepGitUpdated(boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }

    public void dir(File dir) {
        this.dir = dir;
    }

    public void initScript(File initScript) {
        this.initScript = initScript;
    }

    public void dependencyType(Dependency.DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }
}
