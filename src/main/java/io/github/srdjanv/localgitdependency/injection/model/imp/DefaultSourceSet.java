package io.github.srdjanv.localgitdependency.injection.model.imp;

import io.github.srdjanv.localgitdependency.injection.model.SourceSet;

import java.io.Serializable;
import java.util.List;

public class DefaultSourceSet implements SourceSet, Serializable {
    private final String name;
    private final String classpathConfigurationName;
    private final List<String> sources;
    private final List<String> classpathDependencies;

    public DefaultSourceSet(String name, String classpathConfigurationName, List<String> sources, List<String> classpathDependencies) {
        this.name = name;
        this.sources = sources;
        this.classpathConfigurationName = classpathConfigurationName;
        this.classpathDependencies = classpathDependencies;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String classpathConfigurationName() {
        return classpathConfigurationName;
    }

    @Override
    public List<String> getClasspathDependencies() {
        return classpathDependencies;
    }

    @Override
    public List<String> getSources() {
        return sources;
    }
}
