package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public final class DefaultSourceSetMapper extends GroovyObjectSupport implements SourceSetMapper, ConfigFinalizer {
    private final SourceSetContainer container;
    private final String name;
    private final ListProperty<String> depMappings;
    private final Property<SourceSet> targetSourceSet;
    private final Property<Boolean> recursive;

    public DefaultSourceSetMapper(final SourceSetContainer container,
                                  final LGDIDE lgdide,
                                  final Managers managers,
                                  final String name) {
        this.container = container;
        this.name = name;
        depMappings = managers.getProject().getObjects().listProperty(String.class);
        targetSourceSet = managers.getProject().getObjects().property(SourceSet.class);
        recursive = managers.getProject().getObjects().property(Boolean.class);

        depMappings.convention(managers.getProject().provider(()-> lgdide.getDefaultDepMappings().get()));
        targetSourceSet.convention(managers.getProject().provider(()-> lgdide.getDefaultTargetSourceSet().get()));
        recursive.convention(managers.getProject().provider(()-> lgdide.getRecursive().get()));
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public ListProperty<String> getDepMappings() {
        return depMappings;
    }

    @Override
    public Property<SourceSet> getTargetSourceSet() {
        return targetSourceSet;
    }

    @Override
    public Property<Boolean> getRecursive() {
        return recursive;
    }

    @Override
    public void finalizeProps() {
        depMappings.finalizeValue();
        targetSourceSet.finalizeValue();
        recursive.finalizeValue();
    }

    @Override
    public void map(SourceSet manSourceSet, Object... args) {
        targetSourceSet.set(manSourceSet);
        for (Object arg : args) {
            if (arg instanceof CharSequence sequence) {
                depMappings.add(String.valueOf(sequence)); // TODO: 24/08/2023 format
            } else if (arg instanceof SourceSet set) {
                depMappings.add(set.getName());
            } else throw new IllegalArgumentException(String.valueOf(arg));
        }
    }

    @SuppressWarnings("unused")
    public Object propertyMissing(String propertyName) {
        var source = container.findByName(propertyName);
        if (source == null) return propertyName;
        return source;
    }

    @SuppressWarnings("unused")
    public void methodMissing(String name, Object value) {
        if (!(value instanceof Object[] valueArr)) {
            throw new MissingMethodException("map", null, new Object[]{name, value});
        }
        map(container.maybeCreate(name), valueArr);
    }
}
