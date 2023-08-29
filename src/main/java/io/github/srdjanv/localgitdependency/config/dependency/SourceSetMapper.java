package io.github.srdjanv.localgitdependency.config.dependency;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;

public interface SourceSetMapper {
    String getName();

    Property<Boolean> getRecursive();

    void map(SourceSet manSourceSet, Object... args);

    void mappings(Action<NamedDomainObjectContainer<Mapping>> action);

    NamedDomainObjectContainer<Mapping> getMappings();

    interface Mapping {
        String getName();

        Property<SourceSet> getTargetSourceSet();

        ListProperty<String> getDependents();

        Property<Boolean> getRecursive();
    }
}
