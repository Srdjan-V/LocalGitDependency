package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface IDependencyManager extends Manager {
    static IDependencyManager createInstance(Managers managers){
        return new DependencyManager(managers);
    }
    DependencyConfig registerDependency(@NotNull String dependencyURL);
    @TaskDescription("resolving registered dependencies")
    boolean resolveRegisteredDependencies();
    @TaskDescription("adding built dependencies")
    boolean registerRepos();
    @TaskDescription("handling SourceSets")
    boolean handelSourceSets();
    @Unmodifiable
    Set<Dependency> getDependencies();
    void markBuild(String dep, Dependency.Type type);
    @Nullable
    Set<Dependency.Type> getMarkedBuild(String dep);
}
