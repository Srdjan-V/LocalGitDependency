package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.UrlRepository;

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
}
