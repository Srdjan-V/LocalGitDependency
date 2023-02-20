package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyGetters extends CommonPropertyFields {

    public String getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Boolean getKeepGitUpdated() {
        return keepGitUpdated;
    }

    public File getDir() {
        return gitDir;
    }

    public File getPersistentFolder() {
        return persistentFolder;
    }

    public File getMavenFolder() {
        return mavenFolder;
    }

    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    public Boolean getKeepDependencyInitScriptUpdated() {
        return keepDependencyInitScriptUpdated;
    }
}
