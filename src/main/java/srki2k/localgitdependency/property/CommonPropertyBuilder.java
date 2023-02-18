package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

//Base property's for dependency's global configurations
public abstract class CommonPropertyBuilder extends CommonPropertyFields {
    CommonPropertyBuilder() {
    }

    public void defaultConfiguration(String defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void keepGitUpdated(boolean keepGitUpdated) {
        this.keepGitUpdated = keepGitUpdated;
    }

    public void gitDir(File dir) {
        this.gitDir = dir;
    }

    public void persistentFolder(File initScript) {
        this.persistentFolder = initScript;
    }

    public void mavenFolder(File mavenFolder) {
        this.mavenFolder = mavenFolder;
    }

    public void dependencyType(Dependency.Type dependencyType) {
        this.dependencyType = dependencyType;
    }

    public void gradleProbeCashing(boolean gradleProbeCashing) {
        this.gradleProbeCashing = gradleProbeCashing;
    }

}
