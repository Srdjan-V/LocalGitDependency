package srki2k.localgitdependency.property;

import srki2k.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyFields {
    CommonPropertyFields() {
    }

    String defaultConfiguration;
    Boolean keepGitUpdated;
    File gitDir;
    File persistentFolder;
    File mavenLocalFolder;
    Dependency.DependencyType dependencyType;
    Boolean gradleProbeCashing;
}
