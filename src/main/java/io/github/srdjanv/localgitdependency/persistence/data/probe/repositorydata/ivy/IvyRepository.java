package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.UrlRepository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.util.GradleVersion;

import java.util.List;
import java.util.Map;

public class IvyRepository extends UrlRepository implements IIvyRepository {
    public List<String> ivyPatterns;
    public List<String> artifactPatterns;
    public String layoutType;
    public boolean m2Compatible;

    public IvyRepository() {
    }

    public IvyRepository(RepositoryWrapper repositoryWrapper) {
        super(repositoryWrapper);
        Map<String, ?> properties = repositoryWrapper.getProperties();
        ivyPatterns = (List<String>) properties.get("IVY_PATTERNS");
        artifactPatterns = (List<String>) properties.get("ARTIFACT_PATTERNS");
        layoutType = (String) properties.get("LAYOUT_TYPE");
        m2Compatible = (Boolean) properties.get("M2_COMPATIBLE");
    }

    @Override
    public List<String> getIvyPatterns() {
        return ivyPatterns;
    }

    @Override
    public List<String> getArtifactPatterns() {
        return artifactPatterns;
    }

    @Override
    public String getLayoutType() {
        return layoutType;
    }

    @Override
    public boolean isM2Compatible() {
        return m2Compatible;
    }

    @Override
    public Action<? super IvyArtifactRepository> configureAction() {
        return ivy -> {
            ivy.setName(getName());

            if (!getMetadataSources().isEmpty()) {
                if (GradleVersion.version("4.5").compareTo(GradleVersion.current()) >= 0) {
                    ivy.metadataSources(sources -> {
                        for (String metadataSource : getMetadataSources()) {
                            switch (metadataSource) {
                                case "gradleMetadata" -> sources.gradleMetadata();
                                case "ivyDescriptor" -> sources.ivyDescriptor();
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
