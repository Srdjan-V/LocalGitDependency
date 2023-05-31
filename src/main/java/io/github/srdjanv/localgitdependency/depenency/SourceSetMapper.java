package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.SourceSetMapperConfig;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceSetMapper {
    public static List<SourceSetMapper> build(DependencyConfig dependencyConfig) {
        var mappings = new ArrayList<SourceSetMapper>();
        if (dependencyConfig.getMappings() != null) {
            for (var mappingClosure : dependencyConfig.getMappings()) {
                var builder = new SourceSetMapperConfig.Builder();
                ClosureUtil.delegate(mappingClosure, builder);
                mappings.add(new SourceSetMapper(new SourceSetMapperConfig(builder)));
            }
        }
        return Collections.unmodifiableList(mappings);
    }

    private final String projectSet;
    private final String[] dependencySet;
    private final boolean recursive;

    public SourceSetMapper(SourceSetMapperConfig config) {
        this.projectSet = config.getProjectSet();
        this.dependencySet = config.getDependencySet();
        this.recursive = config.isRecursive() != null ? config.isRecursive() : true;
    }

    public String getProjectSet() {
        return projectSet;
    }

    public String[] getDependencySet() {
        return dependencySet;
    }

    public boolean isRecursive() {
        return recursive;
    }
}
