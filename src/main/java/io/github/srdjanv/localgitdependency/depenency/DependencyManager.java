package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDependencyConfig;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import java.io.File;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

// TODO: 29/07/2023 rewrite
final class DependencyManager extends ManagerBase implements IDependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();
    private final List<DependencyConfig> unResolvedDependencies = new ArrayList<>();
    private final Map<String, Set<Dependency.Type>> buildMarkers = new HashMap<>();

    DependencyManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {}

    @Override
    public DependencyConfig registerDependency(@NotNull final String dependencyURL) {
        Objects.requireNonNull(dependencyURL, "dependencyURL can`t be null");
        var dep = getProject().getObjects().newInstance(DefaultDependencyConfig.class, dependencyURL, this);
        unResolvedDependencies.add(dep);
        return dep;
    }

    @Override
    public boolean resolveRegisteredDependencies() {
        for (var dependencyConfig : unResolvedDependencies) {
            ((DefaultDependencyConfig) dependencyConfig).finalizeProps();
            dependencies.add(new Dependency(this, dependencyConfig));
        }
        unResolvedDependencies.clear();
        buildMarkers.clear();
        return !dependencies.isEmpty();
    }

    @Override
    public boolean registerRepos() {
        boolean didWork = false;
        for (Dependency dependency : dependencies) {
            if (dependency.getBuildTargets().contains(Dependency.Type.JarFlatDir)) {
                didWork = true;
                flatDirRepos(dependency);
            }
        }
        return didWork;
    }

    @Override
    public void markBuild(String dep, Dependency.Type type) {
        buildMarkers.computeIfAbsent(dep, d -> new HashSet<>()).add(type);
    }

    @Override
    public @Nullable Set<Dependency.Type> getMarkedBuild(String dep) {
        return buildMarkers.get(dep);
    }

    private void flatDirRepos(Dependency dependency) {
        final File libs = Constants.buildDir.apply(dependency.getGitInfo().getDir());

        if (!libs.exists()) {
            ManagerLogger.error("Dependency: {}, no libs folder was found", dependency.getName());
            return;
        }

        if (dependency.shouldRegisterRepository()) {
            final var name = Constants.RepositoryFlatDir.apply(dependency);
            ManagerLogger.info("Adding {} repo at {} for dependency: {}", name, libs, dependency.getName());
            getProject().getRepositories().flatDir(flatDir -> {
                flatDir.setName(name);
                flatDir.dir(libs);
            });
        } else {
            ManagerLogger.info(
                    "Skipping registration of {} repository for dependency: {}",
                    dependency.getBuildTargets(),
                    dependency.getName());
        }
    }

    @Override
    @Unmodifiable
    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
