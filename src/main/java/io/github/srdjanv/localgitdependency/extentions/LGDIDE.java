package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultSourceSetMapper;
import io.github.srdjanv.localgitdependency.project.Managers;
import java.util.Collections;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class LGDIDE extends GroovyObjectSupport {
    public static final String NAME = "lgdide";
    private final Managers managers;
    private final NamedDomainObjectContainer<SourceSetMapper> mappers;

    /**
     * By enabling this the plugin will register the source sets, configurations, its dependencies to your project.
     * <p>
     * Disabled by default
     *
     * @param enableIdeSupport if it should enable ide support
     */
    private final Property<Boolean> enableIdeSupport;

    private final Property<Boolean> recursive;
    private final Property<SourceSet> defaultTargetSourceSet;
    private final ListProperty<String> defaultDepMappings;

    @Inject
    public LGDIDE(final Managers managers) {
        this.managers = managers;
        final var container = managers.getProject().getExtensions().getByType(SourceSetContainer.class);
        this.mappers = managers.getProject().getObjects().domainObjectContainer(SourceSetMapper.class, name -> {
            return managers.getProject()
                    .getObjects()
                    .newInstance(DefaultSourceSetMapper.class, container, this, managers, name);
        });

        enableIdeSupport = managers.getProject().getObjects().property(Boolean.class);
        recursive = managers.getProject().getObjects().property(Boolean.class);

        defaultTargetSourceSet = managers.getProject().getObjects().property(SourceSet.class);
        defaultDepMappings = managers.getProject().getObjects().listProperty(String.class);

        enableIdeSupport.convention(false);
        recursive.convention(true);
        defaultTargetSourceSet.convention(container.findByName(SourceSet.MAIN_SOURCE_SET_NAME));
        defaultDepMappings.convention(Collections.singleton(SourceSet.MAIN_SOURCE_SET_NAME));
    }

    public void mappers(Action<NamedDomainObjectContainer<SourceSetMapper>> action) {
        action.execute(mappers);
    }

    public NamedDomainObjectContainer<SourceSetMapper> getMappers() {
        return mappers;
    }

    public Property<Boolean> getEnableIdeSupport() {
        return enableIdeSupport;
    }

    public Property<Boolean> getRecursive() {
        return recursive;
    }

    public Property<SourceSet> getDefaultTargetSourceSet() {
        return defaultTargetSourceSet;
    }

    public ListProperty<String> getDefaultDepMappings() {
        return defaultDepMappings;
    }
}
