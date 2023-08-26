package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultSourceSetMapper;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSetContainer;

import javax.inject.Inject;

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
    private Property<Boolean> enableIdeSupport;


    @Inject
    public LGDIDE(final Managers managers) {
        this.managers = managers;
        final var container = managers.getProject().getExtensions().getByType(SourceSetContainer.class);
        this.mappers = managers.getProject().getObjects().domainObjectContainer(SourceSetMapper.class, name -> {
            return new DefaultSourceSetMapper(container, managers, name);
        });
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
}
