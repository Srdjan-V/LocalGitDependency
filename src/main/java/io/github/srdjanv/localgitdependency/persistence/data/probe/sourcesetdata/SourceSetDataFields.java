package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import java.util.List;

class SourceSetDataFields {
    String name;
    String classpathConfigurationName;
    List<String> repositoryClasspathDependencies;
    List<String> fileClasspathDependencies;
    List<String> sources;
}
