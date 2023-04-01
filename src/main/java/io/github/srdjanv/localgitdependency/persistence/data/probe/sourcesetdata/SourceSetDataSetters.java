package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import java.util.List;

public interface SourceSetDataSetters {
    void setName(String name);
    void setClasspathConfigurationName(String classpathConfigurationName);
    void setRepositoryClasspathDependencies(List<String> repositoryClasspathDependencies);
    void setFileClasspathDependencies(List<String> fileClasspathDependencies);
    void setSources(List<String> sources);
}
