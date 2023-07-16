package io.github.srdjanv.localgitdependency.injection.model;

import java.io.Serializable;

public final class DefaultLocalGitDependencyJsonInfoModel implements LocalGitDependencyJsonInfoModel, Serializable {
    private final String json;

    public DefaultLocalGitDependencyJsonInfoModel(String json) {
        this.json = json;
    }

    @Override
    public String getJson() {
        return json;
    }
}
