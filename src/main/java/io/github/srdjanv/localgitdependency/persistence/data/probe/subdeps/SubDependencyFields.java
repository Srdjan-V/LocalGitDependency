package io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;

public class SubDependencyFields {
    String name;
    String projectID;
    String archivesBaseName;
    Dependency.Type dependencyType;
    String gitDir;

    @NullableData
    String mavenFolder;
}
