package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.ArrayList;
import java.util.List;

public final class SourceSetMapper extends GroovyObjectSupport {
    private final SourceSetContainer container;
    private final String name;
    private final List<String> mappings;
    private SourceSet sourceSet;
    private final Property<Boolean> recursive;

    public SourceSetMapper(final SourceSetContainer container, final Managers managers, final String name) {
        this.container = container;
        this.name = name;
        mappings = new ArrayList<>(5);
        recursive = managers.getProject().getObjects().property(Boolean.class); // TODO: 25/08/2023
    }

    public String getName() {
        return name;
    }

    public List<String> getMappings() {
        return mappings;
    }

    public SourceSet getSourceSet() {
        return sourceSet;
    }

    public Property<Boolean> getRecursive() {
        return recursive;
    }

    public void map(SourceSet manSourceSet, Object... args) {
        sourceSet = manSourceSet;
        for (Object arg : args) {
            if (arg instanceof CharSequence sequence) {
                mappings.add(String.valueOf(sequence)); // TODO: 24/08/2023 format
            } else if (arg instanceof SourceSet set) {
                mappings.add(set.getName());
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
