package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.SourceSetMapperConfig;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceSetMapper {
    public static List<SourceSetMapper> build(DependencyConfig dependencyConfig, ErrorUtil errorBuilder) {
        var mappings = new ArrayList<SourceSetMapper>();
        if (dependencyConfig.getMappings() != null) {
            for (var mappingClosure : dependencyConfig.getMappings()) {
                var builder = new SourceSetMapperConfig.Builder();
                if (ClosureUtil.delegateNullSafe(mappingClosure, builder)) {
                    mappings.add(new SourceSetMapper(new SourceSetMapperConfig(builder)));
                } else {
                    errorBuilder.append("DependencyConfig: A SourceSetMapperBuilder is null");
                }
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
