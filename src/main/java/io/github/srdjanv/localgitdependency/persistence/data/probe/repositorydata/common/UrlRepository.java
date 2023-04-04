package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class UrlRepository extends Repository implements IUrlRepository {
    private URI url;
    private List<String> metadataSources;
    private boolean authenticated;
    private List<String> authenticationSchemes;

    public UrlRepository() {
    }

    public UrlRepository(RepositoryWrapper repositoryWrapper) {
        super(repositoryWrapper);
        Map<String, ?> properties = repositoryWrapper.getProperties();
        url = (URI) properties.get("URL");
        metadataSources = (List<String>) properties.get("METADATA_SOURCES");
        authenticated = (Boolean) properties.get("AUTHENTICATED");
        authenticationSchemes = (List<String>) properties.get("AUTHENTICATION_SCHEMES");
    }

    @Override
    public URI getUrl() {
        return url;
    }

    @Override
    public List<String> getMetadataSources() {
        return metadataSources;
    }

    @Override
    public boolean getAuthenticated() {
        return authenticated;
    }

    @Override
    public List<String> getAuthenticationSchemes() {
        return authenticationSchemes;
    }
}
