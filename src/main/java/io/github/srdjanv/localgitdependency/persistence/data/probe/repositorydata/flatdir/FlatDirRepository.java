package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;

import java.io.File;
import java.util.List;

public class FlatDirRepository extends Repository implements IFlatDirRepository, IRepository {
    public List<File> dirs;

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
}
