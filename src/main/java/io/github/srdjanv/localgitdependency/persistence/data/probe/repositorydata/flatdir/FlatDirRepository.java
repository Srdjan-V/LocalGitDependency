package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository;

import java.io.File;
import java.util.List;

public class FlatDirRepository extends Repository implements IFlatDirRepository, IRepository {
    private List<File> dirs;

    public FlatDirRepository() {
    }

    public FlatDirRepository(RepositoryWrapper repositoryWrapper) {
        super(repositoryWrapper);
        dirs = (List<File>) repositoryWrapper.getProperties().get("DIRS");
    }

    @Override
    public List<File> getDirs() {
        return dirs;
    }

    @Override
    public Action<? super FlatDirectoryArtifactRepository> configureAction() {
        return flatDir -> {
            flatDir.setName(getName());
            flatDir.dirs(getDirs());
        };
    }
}
