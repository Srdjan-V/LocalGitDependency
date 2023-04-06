package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface IDependencyManager extends Manager {
    static IDependencyManager createInstance(Managers managers){
        return new DependencyManager(managers);
    }
    void registerDependency(String configurationName, String dependencyURL, @SuppressWarnings("rawtypes") Closure configureClosure);
    @TaskDescription("adding built dependencies")
    void addBuiltDependencies();
    @Unmodifiable
    Set<Dependency> getDependencies();
}
