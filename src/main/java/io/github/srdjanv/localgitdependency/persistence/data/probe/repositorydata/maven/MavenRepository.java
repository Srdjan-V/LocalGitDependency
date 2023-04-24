package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.UrlRepository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.util.GradleVersion;

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

    @Override
    public Action<? super MavenArtifactRepository> configureAction() {
        return maven -> {
            maven.setName(getName());
            maven.setUrl(getUrl());
            if (!getArtifactUrls().isEmpty()) {
                maven.artifactUrls(getArtifactUrls());
            }

            if (!getMetadataSources().isEmpty()) {
                if (GradleVersion.version("4.5").compareTo(GradleVersion.current()) >= 0) {
                    maven.metadataSources(sources -> {
                        for (String metadataSource : getMetadataSources()) {
                            switch (metadataSource) {
                                case "gradleMetadata" -> sources.gradleMetadata();
                                case "mavenPom" -> sources.mavenPom();
                                case "artifact" -> sources.artifact();
                                case "ignoreGradleMetadataRedirection" -> sources.ignoreGradleMetadataRedirection();
                            }
                        }
                    });
                }
            }
        };
    }
}
