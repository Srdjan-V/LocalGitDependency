package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.project.Managers;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public abstract class DefaultSourceSetMapper extends GroovyObjectSupport implements SourceSetMapper, ConfigFinalizer {
    private final SourceSetContainer container;
    private final Managers managers;
    private final LGDIDE lgdide;
    private final String name;
    private final Property<Boolean> recursive;
    private final NamedDomainObjectContainer<Mapping> mappings;

    @Inject
    public DefaultSourceSetMapper(
            final SourceSetContainer container, final LGDIDE lgdide, final Managers managers, final String name) {
        this.container = container;
        this.managers = managers;
        this.lgdide = lgdide;
        this.name = name;
        recursive = managers.getProject().getObjects().property(Boolean.class);

        recursive.convention(
                managers.getProject().provider(() -> lgdide.getRecursive().get()));
        mappings = managers.getProject().getObjects().domainObjectContainer(Mapping.class, named -> {
            return managers.getProject()
                    .getObjects()
                    .newInstance(DefaultMapping.class, named, managers, container, this);
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Property<Boolean> getRecursive() {
        return recursive;
    }

    @Override
    public void finalizeProps() {
        recursive.finalizeValue(); // TODO: 28/08/2023
    }

    @Override
    public void map(SourceSet sourceSet, Object... args) {
        var mapping = managers.getProject()
                .getObjects()
                .newInstance(DefaultMapping.class, sourceSet.getName(), managers, this);
        mappings.add(mapping);
        for (Object arg : args) {
            if (arg instanceof CharSequence sequence) {
                mapping.dependents.add(String.valueOf(sequence));
            } else if (arg instanceof SourceSet set) {
                mapping.dependents.add(set.getName());
            } else if (arg instanceof Closure<?> closure) {
                closure.setDelegate(mapping);
                closure.setResolveStrategy(Closure.DELEGATE_ONLY);
                closure.call();
            } else throw new IllegalArgumentException(String.valueOf(arg));
        }
    }

    @Override
    public void mappings(Action<NamedDomainObjectContainer<Mapping>> action) {
        action.execute(mappings);
    }

    @Override
    public NamedDomainObjectContainer<Mapping> getMappings() {
        return mappings;
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
            throw new MissingMethodException("map", null, new Object[] {name, value});
        }
        map(container.maybeCreate(name), valueArr);
    }

    public static class DefaultMapping extends GroovyObjectSupport implements Mapping {
        private final Property<SourceSet> targetSourceSet;
        private final ListProperty<String> dependents;
        private final Property<Boolean> recursive;

        @Inject
        public DefaultMapping(
                final String name,
                final Managers managers,
                final SourceSetContainer container,
                final DefaultSourceSetMapper mapper) {
            targetSourceSet = managers.getProject().getObjects().property(SourceSet.class);
            targetSourceSet.value(container.findByName(name)).finalizeValue();

            dependents = managers.getProject().getObjects().listProperty(String.class);
            recursive = managers.getProject().getObjects().property(Boolean.class);

            dependents.convention(managers.getProject()
                    .provider(() -> mapper.lgdide.getDefaultDepMappings().get()));
            recursive.convention(
                    managers.getProject().provider(() -> mapper.getRecursive().get()));
        }

        @Override
        public String getName() {
            return targetSourceSet.get().getName();
        }

        @Override
        public Property<SourceSet> getTargetSourceSet() {
            return targetSourceSet;
        }

        @Override
        public ListProperty<String> getDependents() {
            return dependents;
        }

        @Override
        public Property<Boolean> getRecursive() {
            return recursive;
        }
    }
}
