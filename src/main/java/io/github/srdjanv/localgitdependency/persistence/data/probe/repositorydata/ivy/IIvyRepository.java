package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IUrlRepository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;

import java.util.List;

public interface IIvyRepository extends IRepository, IUrlRepository {
    List<String> getIvyPatterns();
    List<String> getArtifactPatterns();
    String getLayoutType();
    boolean isM2Compatible();
    Action<? super IvyArtifactRepository> configureAction();
}
