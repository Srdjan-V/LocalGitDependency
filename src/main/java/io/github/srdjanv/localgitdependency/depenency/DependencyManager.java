package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDependencyConfig;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static io.github.srdjanv.localgitdependency.Constants.TASKS_GROUP_INTERNAL;

// TODO: 29/07/2023 rewrite
final class DependencyManager extends ManagerBase implements IDependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();
    private final List<DependencyConfig> unResolvedDependencies = new ArrayList<>();
    private final Map<String, Set<Dependency.Type>> buildMarkers = new HashMap<>();

    private SourceSetContainer rootSourceSetContainer;
    private Function<SourceSet, String[]> taskSupplier;

    DependencyManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        rootSourceSetContainer = getProject().getRootProject().getExtensions().getByType(SourceSetContainer.class);
        //this is used to disable tasks for class compilation and so on
        if (GradleVersion.version(getProject().getGradle().getGradleVersion()).compareTo(GradleVersion.version("6.0")) >= 0) {
            taskSupplier = (sourceSet) -> new String[]{
                    sourceSet.getCompileJavaTaskName(),
                    sourceSet.getProcessResourcesTaskName(),
                    sourceSet.getClassesTaskName(),
                    sourceSet.getJavadocJarTaskName(),
                    sourceSet.getSourcesJarTaskName()};
        } else {
            taskSupplier = (sourceSet) -> new String[]{
                    sourceSet.getCompileJavaTaskName(),
                    sourceSet.getProcessResourcesTaskName(),
                    sourceSet.getClassesTaskName()};
        }
    }

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
            var builds = buildMarkers.get(dependencyConfig.getName().get());
            dependencyConfig.getBuildTargets().addAll(builds);
            dependencies.add(new Dependency(this, dependencyConfig));
        }
        unResolvedDependencies.clear();
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
    public boolean handelSourceSets() {
        boolean didWork = false;
        for (Dependency dependency : dependencies) {
            if (dependency.isIdeSupportEnabled()) {
                didWork = true;
                handleIdeSupport(dependency);
            }
        }
        return didWork;
    }

    @Override
    public void markBuild(String dep, Dependency.Type type) {
        buildMarkers.computeIfAbsent(dep, d -> new HashSet<>()).add(type);
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
            ManagerLogger.info("Skipping registration of {} repository for dependency: {}", dependency.getBuildTargets(), dependency.getName());
        }
    }


    private void handleIdeSupport(Dependency dependency) {
        var rootProject = getProject().getRootProject();
        //create source sets of the dependency
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            var sourceSet = rootSourceSetContainer.create(getSourceSetName(dependency, sourceSetData), sourceSetConf -> {
                sourceSetConf.java(conf -> {
                    conf.setSrcDirs(sourceSetData.getSources());
                    conf.getDestinationDirectory().set(rootProject.file(sourceSetData.getBuildClassesDir()));
                });
                sourceSetConf.resources(conf -> {
                    conf.setSrcDirs(sourceSetData.getResources());
                    conf.getDestinationDirectory().set(rootProject.file(sourceSetData.getBuildResourcesDir()));
                });
            });

            for (String task : taskSupplier.apply(sourceSet)) {
                try {
                    var sourceTask = rootProject.getTasks().getByName(task);
                    sourceTask.setEnabled(false);
                    sourceTask.setGroup(TASKS_GROUP_INTERNAL);
                } catch (UnknownTaskException ignore) {
                }
            }
        }
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            SourceSet depSourceSet = getSourceSetByName(rootSourceSetContainer, dependency, sourceSetData);

            // configure source set classpath
            if (!sourceSetData.getCompileClasspath().isEmpty()) {
                depSourceSet.setCompileClasspath(rootProject.getLayout().files(sourceSetData.getCompileClasspath()));
            }

            // link source sets to each other
            for (String dependentSourceSetName : sourceSetData.getDependentSourceSets()) {
                final Optional<SourceSetData> data = dependency.getPersistentInfo().getProbeData().getSourceSetsData().
                        stream().filter(probe -> probe.getName().equals(dependentSourceSetName)).findFirst();

                if (data.isPresent()) {
                    final SourceSetData sourceData = data.get();
                    final var set = getSourceSetByName(rootSourceSetContainer, dependency, sourceData);

                    depSourceSet.setCompileClasspath(depSourceSet.getCompileClasspath().
                            plus(set.getOutput()));
                }
            }

            //link source sets to the main project using mappers
            SourceSetMapper sourceSetMapper = dependency.getSourceSetMapper();
            final Set<String> dependentSourceSets = new HashSet<>();
            for (String dependentSourceSetName : sourceSetMapper.getDepMappings().get()) {
                if (sourceSetData.getName().equals(dependentSourceSetName)) {
                    dependentSourceSets.add(dependentSourceSetName);
                    if (sourceSetMapper.getRecursive().get()) {
                        dependentSourceSets.addAll(sourceSetData.getDependentSourceSets());
                    }
                }
            }

            for (String dependentSourceSetName : dependentSourceSets) {
                final SourceSet projectSet;
                if (getProject() == rootProject) {
                    projectSet = sourceSetMapper.getTargetSourceSet().get();
                } else {
                    var subProjectSourceSetContainer = getProject().getExtensions().getByType(SourceSetContainer.class);
                    projectSet = subProjectSourceSetContainer.getByName(sourceSetMapper.getTargetSourceSet().get().getName()); // TODO: 25/08/2023 probably not needed, test
                }

                final var sourceData = dependency.getPersistentInfo().getProbeData().getSourceSetsData().
                        stream().filter(probe -> probe.getName().equals(dependentSourceSetName)).findFirst().
                        orElseThrow(() -> new IllegalArgumentException(
                                String.format("Source set %s not found for dependency %s", dependentSourceSetName, dependency.getName())));

                final var set = getSourceSetByName(rootSourceSetContainer, dependency, sourceData);
                projectSet.setCompileClasspath(projectSet.getCompileClasspath().
                        plus(set.getOutput()));
            }
        }
    }

    private SourceSet getSourceSetByName(SourceSetContainer sourceSetContainer, Dependency dependency, SourceSetData sourceSetData) {
        return sourceSetContainer.getByName(getSourceSetName(dependency, sourceSetData));
    }

    private String getSourceSetName(Dependency dependency, SourceSetData sourceSetData) {
        return Constants.EXTENSION_NAME + "." + getProject().getName() + "." + dependency.getName() + "." + sourceSetData.getName();
    }

    @Override
    @Unmodifiable
    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
