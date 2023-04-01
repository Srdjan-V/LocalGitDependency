package io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata;

import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;

import java.util.List;
import java.util.function.Consumer;

public class SourceSetData implements SourceSetDataGetters, SourceSetDataSetters, NonNullData {
    private String name;
    private String classpathConfigurationName;
    private List<String> repositoryClasspathDependencies;
    private List<String> fileClasspathDependencies;
    private List<String> sources;

    public SourceSetData() {
    }

    public static SourceSetData create(Consumer<SourceSetDataSetters> configuration) {
        SourceSetData instance = new SourceSetData();
        configuration.accept(instance);
        return instance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getClasspathConfigurationName() {
        return classpathConfigurationName;
    }

    @Override
    public void setClasspathConfigurationName(String classpathConfigurationName) {
        this.classpathConfigurationName = classpathConfigurationName;
    }

    @Override
    public List<String> getRepositoryClasspathDependencies() {
        return repositoryClasspathDependencies;
    }

    @Override
    public void setRepositoryClasspathDependencies(List<String> repositoryClasspathDependencies) {
        this.repositoryClasspathDependencies = repositoryClasspathDependencies;
    }

    @Override
    public List<String> getFileClasspathDependencies() {
        return fileClasspathDependencies;
    }

    @Override
    public void setFileClasspathDependencies(List<String> fileClasspathDependencies) {
        this.fileClasspathDependencies = fileClasspathDependencies;
    }

    @Override
    public List<String> getSources() {
        return sources;
    }

    @Override
    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}
