package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common;

import java.net.URI;
import java.util.List;

public interface IUrlRepository {
    URI getUrl();
    List<String> getMetadataSources();
    boolean getAuthenticated();
    List<String> getAuthenticationSchemes();
}
