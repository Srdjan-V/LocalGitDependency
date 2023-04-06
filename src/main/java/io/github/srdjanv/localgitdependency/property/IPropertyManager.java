package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;

public interface IPropertyManager extends Manager {
    static IPropertyManager createInstance(Managers managers) {
        return new PropertyManager(managers);
    }

    void globalProperty(@SuppressWarnings("rawtypes") Closure configureClosure);
    GlobalProperty getGlobalProperty();
    @TaskDescription("create essential directories")
    void createEssentialDirectories();
    void applyDefaultProperty(DependencyProperty dependencyDependencyProperty);
}
