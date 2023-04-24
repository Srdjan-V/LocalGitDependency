package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir;

import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository;

import java.io.File;
import java.util.List;

public interface IFlatDirRepository {
    List<File> getDirs();
    Action<? super FlatDirectoryArtifactRepository> configureAction();
}
