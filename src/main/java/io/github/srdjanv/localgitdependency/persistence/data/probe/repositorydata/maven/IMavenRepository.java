package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IUrlRepository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import java.net.URI;
import java.util.List;

public interface IMavenRepository extends IRepository, IUrlRepository {
    List<URI> getArtifactUrls();
    Action<? super MavenArtifactRepository> configureAction();
}
