package io.github.srdjanv.localgitdependency.config.dependency;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;

public interface SourceSetMapper {
    String getName();

    ListProperty<String> getDepMappings();

    Property<SourceSet> getTargetSourceSet();

    Property<Boolean> getRecursive();

    void map(SourceSet manSourceSet, Object... args);
}
