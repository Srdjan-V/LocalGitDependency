package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.UrlRepository;

import java.net.URI;
import java.util.List;

public class MavenRepository extends UrlRepository implements IMavenRepository {
    private List<URI> artifactUrls;

    public MavenRepository() {
    }

    public MavenRepository(RepositoryWrapper repositoryWrapper) {
        super(repositoryWrapper);
        artifactUrls = (List<URI>) repositoryWrapper.getProperties().get("ARTIFACT_URLS");
    }

    @Override
    public List<URI> getArtifactUrls() {
        return artifactUrls;
    }

}
