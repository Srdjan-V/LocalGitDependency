package com.srdjanv.localgitdependency.property;

import com.srdjanv.localgitdependency.depenency.Dependency;

import java.io.File;

public abstract class CommonPropertyFields {
    CommonPropertyFields() {
    }

    String defaultConfiguration;
    Boolean keepGitUpdated;
    Boolean keepDependencyInitScriptUpdated;
    File gitDir;
    File javaHomeDir;
    File persistentFolder;
    File mavenFolder;
    Dependency.Type dependencyType;
}
