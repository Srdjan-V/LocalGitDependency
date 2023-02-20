package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyFields {
    CommonPropertyFields() {
    }

    String defaultConfiguration;
    Boolean keepGitUpdated;
    Boolean keepDependencyInitScriptUpdated;
    File gitDir;
    File persistentFolder;
    File mavenFolder;
    Dependency.Type dependencyType;
}
