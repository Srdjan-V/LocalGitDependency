package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyFields {
    CommonPropertyFields() {
    }

    String defaultConfiguration;
    Boolean manualBuild;
    Boolean keepGitUpdated;
    File dir;
    File persistentFolder;
    Dependency.DependencyType dependencyType;
}
