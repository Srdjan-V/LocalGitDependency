package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import java.util.List;
import java.util.Set;

class SourceSetDataFields {
    String name;
    String buildClassesDir;
    String buildResourcesDir;
    Set<String> dependentSourceSets;
    List<String> compileClasspath;
    List<String> sources;
    List<String> resources;
}
