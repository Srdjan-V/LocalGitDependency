package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import java.util.List;

public interface SourceSetDataGetters {
    String getName();
    String getClasspathConfigurationName();
    List<String> getRepositoryClasspathDependencies();
    List<String> getFileClasspathDependencies();
    List<String> getSources();
}
