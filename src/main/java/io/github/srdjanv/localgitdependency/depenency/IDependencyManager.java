package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IDependencyManager extends Manager {
    static IDependencyManager createInstance(Managers managers) {
        return new DependencyManager(managers);
    }

    DependencyConfig registerDependency(@NotNull String dependencyURL);

    @TaskDescription("resolving registered dependencies")
    boolean resolveRegisteredDependencies();

    @TaskDescription("adding built dependencies")
    boolean registerRepos();

    @Unmodifiable
    Set<Dependency> getDependencies();

    void tagDep(String notation, Dependency.Type type);

    @Nullable Set<Dependency.Type> getDepTags(String depName);
}
