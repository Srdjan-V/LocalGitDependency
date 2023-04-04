package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata;

import org.gradle.api.Project;
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository;
import org.gradle.api.internal.artifacts.repositories.descriptor.RepositoryDescriptor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryWrapper {
    private final RepositoryDescriptor descriptor;

    static List<RepositoryWrapper> transform(Project project) {
        List<RepositoryWrapper> repositoryWrapperList;
        repositoryWrapperList = project.getRepositories().stream().
                filter(repository -> repository instanceof ResolutionAwareRepository).
                map(repository -> (ResolutionAwareRepository) repository).
                map(resolutionAwareRepository -> new RepositoryWrapper(resolutionAwareRepository.getDescriptor())).
                collect(Collectors.toList());

        return repositoryWrapperList;
    }

    private RepositoryWrapper(RepositoryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return descriptor.name;
    }

    public String getType() {
        return descriptor.getType().name();
    }

    public Map<String, ?> getProperties() {
        return descriptor.getProperties();
    }

}
