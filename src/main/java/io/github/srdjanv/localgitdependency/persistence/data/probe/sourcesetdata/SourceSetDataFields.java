package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;

import java.util.List;
import java.util.Set;

class SourceSetDataFields {
    String name;
    Set<String> dependentSourceSets;
    List<String> compileClasspath;
    String buildResourcesDir;
    List<String> resources;
    List<DirectorySetData> directorySets;
}
