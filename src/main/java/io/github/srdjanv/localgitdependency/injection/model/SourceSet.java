package io.github.srdjanv.localgitdependency.injection.model;

import java.util.List;

public interface SourceSet {
    String getName();
    String classpathConfigurationName();
    List<String> getRepositoryClasspathDependencies();
    List<String> getFileClasspathDependencies();
    List<String> getSources();
}
