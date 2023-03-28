package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface IDependencyManager extends Managers {
    static IDependencyManager createInstance(ProjectInstances projectInstances){
        return new DependencyManager(projectInstances);
    }
    void registerDependency(String configurationName, String dependencyURL, Closure<DependencyProperty.Builder> configureClosure);
    void addBuiltDependencies();
    @Unmodifiable
    Set<Dependency> getDependencies();
}
