package io.github.srdjanv.localgitdependency.injection.model.imp;

import io.github.srdjanv.localgitdependency.injection.model.SourceSet;

import java.io.Serializable;
import java.util.List;

public class DefaultSourceSet implements SourceSet, Serializable {
    private final String name;
    private final List<String> sources;

    public DefaultSourceSet(String name, List<String> sources) {
        this.name = name;
        this.sources = sources;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSources() {
        return sources;
    }
}
