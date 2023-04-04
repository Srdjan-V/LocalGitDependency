package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common;

import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryWrapper;

public class Repository implements IRepository {
    private String type;
    private String name;

    public Repository() {
    }

    public Repository(RepositoryWrapper repositoryWrapper) {
        type = repositoryWrapper.getType();
        name = repositoryWrapper.getName();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

}
