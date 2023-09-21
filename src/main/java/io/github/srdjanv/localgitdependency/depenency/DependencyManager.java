package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDependencyConfig;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

final class DependencyManager extends ManagerBase implements IDependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();
    private final List<DependencyConfig> unResolvedDependencies = new ArrayList<>();
    private final Map<String, Map<String, Set<Dependency.Type>>> tags = new HashMap<>();
    private final Map<String, Consumer<DependencyConfig>> resolutionCallbacks = new HashMap<>();

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
            var config = resolutionCallbacks.get(dependencyConfig.getName().get());
            if (config != null) config.accept(dependencyConfig);
            ((DefaultDependencyConfig) dependencyConfig).finalizeProps();
            var dep = new Dependency(this, dependencyConfig);
            dependencies.add(dep);
        }
        unResolvedDependencies.clear();
        return !dependencies.isEmpty();
    }

    @Override
    public boolean registerRepos() {
        boolean didWork = false;
        boolean mavenLocal = false;
        for (Dependency dependency : dependencies) {
            if (dependency.getBuildTags().contains(Dependency.Type.JarFlatDir)) {
                didWork = true;
                flatDirRepos(dependency);
            }
            if (dependency.getBuildTags().contains(Dependency.Type.MavenLocal)) didWork = mavenLocal = true;

            for (SubDependencyData subDependency :
                    dependency.getPersistentInfo().getProbeData().getSubDependencyData()) {
                var subTags = getDepTags(dependency.getName() + "." + subDependency.getName());
                if (subTags == null) continue;

                if (subTags.contains(Dependency.Type.JarFlatDir)) {
                    didWork = true;
                    flatDirRepos(subDependency);
                }
                if (subTags.contains(Dependency.Type.MavenLocal)) didWork = mavenLocal = true;
            }
        }
        if (mavenLocal) getProject().getRepositories().mavenLocal();

        return didWork;
    }

    @Override
    public void tagDep(String notation, Dependency.Type type) {
        notation = notation.split(":")[0];

        var notationMap = tags.computeIfAbsent(getDepName(notation, true), d -> new HashMap<>());
        notationMap
                .computeIfAbsent(getDepName(notation, false), m -> new HashSet<>())
                .add(type);
    }

    @Override
    public void registerResolutionCallback(String dep, Consumer<DependencyConfig> config) {
        resolutionCallbacks.put(dep, config);
    }

    @Override
    public void tagDep(String notation, String type) {
        tagDep(notation, Dependency.Type.valueOf(type));
    }

    @Override
    public @Nullable Set<Dependency.Type> getDepTags(String depName) {
        var depTags = tags.get(getDepName(depName, true));
        if (depTags == null) return null;
        for (Map.Entry<String, Set<Dependency.Type>> notationSetEntry : depTags.entrySet()) {
            if (depName.equals(notationSetEntry.getKey())) return notationSetEntry.getValue();
        }
        return null;
    }

    @Override
    public @Nullable Map<String, Set<Dependency.Type>> getSubDepTags(String depName) {
        var topDep = getDepName(depName, true);
        var depTags = tags.get(topDep);
        if (depTags == null) return null;
        Map<String, Set<Dependency.Type>> ret = null;
        for (Map.Entry<String, Set<Dependency.Type>> notationSetEntry : depTags.entrySet()) {
            if (notationSetEntry.getKey().equals(topDep)) continue;

            if (ret == null) ret = new HashMap<>();
            ret.put(notationSetEntry.getKey(), notationSetEntry.getValue());
        }
        return ret;
    }

    private String getDepName(String notation, boolean topDep) {
        var charAt = notation.indexOf('.');
        if (charAt == -1) return notation;

        if (topDep) {
            return notation.substring(0, charAt);
        } else return notation.substring(charAt + 1);
    }

    private void flatDirRepos(SubDependencyData subDep) {
        flatDirRepos(subDep.getName(), new File(subDep.getGitDir()));
    }

    private void flatDirRepos(Dependency dependency) {
        flatDirRepos(dependency.getName(), dependency.getGitInfo().getDir());
    }

    private void flatDirRepos(final String name, final File gitDir) {
        final File libs = FileUtil.toBuildDir(gitDir);

        if (!libs.exists()) {
            ManagerLogger.error("Dependency: {}, no libs folder was found", name);
            return;
        }

        final var repoName = name + "FlatDir";
        ManagerLogger.info("Adding {} repo at {} for dependency: {}", repoName, libs.getAbsolutePath(), name);
        getProject().getRepositories().flatDir(flatDir -> {
            flatDir.setName(repoName);
            flatDir.dir(libs);
        });
    }

    @Override
    @Unmodifiable
    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
