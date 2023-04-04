package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IUrlRepository;

import java.net.URI;
import java.util.List;

public interface IMavenRepository extends IRepository, IUrlRepository {
    List<URI> getArtifactUrls();
}
