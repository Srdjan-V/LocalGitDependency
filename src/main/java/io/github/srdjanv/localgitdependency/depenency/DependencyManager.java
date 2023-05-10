package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.impl.Artifact;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.SourceSetMapper;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DependencyManager extends ManagerBase implements IDependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();
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
    public void registerDependency(String configurationName, String dependencyURL, @SuppressWarnings("rawtypes") Closure configureClosure) {
        DependencyProperty dependencyProperty;
        {
            DependencyProperty.Builder dependencyPropertyBuilder = new DependencyProperty.Builder(dependencyURL);
            dependencyPropertyBuilder.configuration(configurationName);
            if (configureClosure != null) {
                ClosureUtil.delegate(configureClosure, dependencyPropertyBuilder);
            }
            dependencyProperty = new DependencyProperty(dependencyPropertyBuilder);
            getPropertyManager().applyDefaultProperty(dependencyProperty);
        }

        var configurations = new ArrayList<Artifact>();
        if (dependencyProperty.getConfigurations() == null) {
            if (dependencyProperty.getConfiguration() != null) {
                var artifactBuilder = new Artifact.Builder();
                artifactBuilder.configuration(dependencyProperty.getConfiguration());
                configurations.add(new Artifact(artifactBuilder));
            }
        } else {
            for (var artifactClosure : dependencyProperty.getConfigurations()) {
                var builder = new Artifact.Builder();
                ClosureUtil.delegate(artifactClosure, builder);
                configurations.add(new Artifact(builder));
            }
        }

        var mappings = new ArrayList<SourceSetMapper>();
        if (dependencyProperty.getMappings() != null) {
            for (var mappingClosure : dependencyProperty.getMappings()) {
                var builder = new SourceSetMapper.Builder();
                ClosureUtil.delegate(mappingClosure, builder);
                mappings.add(new SourceSetMapper(builder));
            }
        }

        dependencies.add(new Dependency(configurations, mappings, dependencyProperty));
    }

    @Override
    public void addBuiltDependencies() {
        boolean addRepositoryMavenProjectLocal = false;
        boolean addRepositoryMavenLocal = false;

        for (Dependency dependency : dependencies) {
            if (dependency.isEnableIdeSupport()) {
                enableIdeSupport(dependency);
            }
            switch (dependency.getDependencyType()) {
                case MavenLocal -> {
                    addRepositoryMavenLocal = true;
                    addRepositoryDependency(dependency);
                }
                case MavenProjectLocal -> {
                    addRepositoryMavenProjectLocal = true;
                    addRepositoryDependency(dependency, dependency.getName());
                }
                case MavenProjectDependencyLocal -> {
                    addMavenProjectDependencyLocal(dependency);
                    addRepositoryDependency(dependency, dependency.getName());
                }
                case JarFlatDir -> {
                    addJarsAsFlatDirDependencies(dependency);
                    addRepositoryDependency(dependency);
                }
                case Jar -> addJarsAsDependencies(dependency);
                default -> throw new IllegalStateException();
            }
        }

        if (addRepositoryMavenLocal) addRepositoryMavenLocal();
        if (addRepositoryMavenProjectLocal) addRepositoryMavenProjectLocal();
    }

    private void addRepositoryMavenLocal() {
        ManagerLogger.info("Adding MavenLocal repository");
        getProject().getRepositories().mavenLocal();
    }

    private void addRepositoryMavenProjectLocal() {
        final File mavenRepo = Constants.MavenProjectLocal.apply(getPropertyManager().getGlobalProperty().getMavenDir());
        ManagerLogger.info("Adding MavenProjectLocal repository at {}", mavenRepo.getAbsolutePath());
        getProject().getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectLocal);
            mavenArtifactRepository.setUrl(mavenRepo);
        });
    }

    private void addMavenProjectDependencyLocal(Dependency dependency) {
        if (dependency.getMavenFolder() == null) {
            throw new RuntimeException(String.format("Dependency: %s maven folder is null this ideally would not be possible", dependency.getName()));
        }

        final String mavenRepo = dependency.getMavenFolder().getAbsolutePath();
        addRepository(dependency, mavenRepo, artifactRepositories -> {
            artifactRepositories.maven(mavenArtifactRepository -> {
                mavenArtifactRepository.setName(Constants.RepositoryMavenProjectDependencyLocal.apply(dependency));
                mavenArtifactRepository.setUrl(mavenRepo);
            });
        });
    }

    private void addJarsAsFlatDirDependencies(Dependency dependency) {
        final File libs = Constants.buildDir.apply(dependency.getGitInfo().getDir());

        if (!libs.exists()) {
            ManagerLogger.error("Dependency: {}, no libs folder was found", dependency.getName());
            return;
        }

        addRepository(dependency, libs.getAbsolutePath(), artifactRepositories -> {
            artifactRepositories.flatDir(flatDir -> {
                flatDir.setName(Constants.RepositoryFlatDir.apply(dependency));
                flatDir.dir(libs);
            });
        });
    }

    private void addJarsAsDependencies(Dependency dependency) {
        final Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

        if (!Files.exists(libs)) {
            ManagerLogger.error("Dependency {}, no libs folder was found", dependency.getName());
            return;
        }

        final Map<String, Object[]> dependencies = new HashMap<>();
        try (Stream<Path> jars = Files.list(libs)) {
            List<Path> paths = jars.collect(Collectors.toList());
            for (var artifact : dependency.getConfigurations()) {
                if (!artifact.getArtifactProperty().isEmpty()) {
                    List<Path> validJars = new ArrayList<>();
                    for (Path path : paths) {
                        for (Artifact.Property property : artifact.getArtifactProperty()) {
                            if (property.include() && path.getFileName().toString().contains(property.notation())) {
                                validJars.add(path);
                            }
                        }
                    }
                    dependencies.put(artifact.getConfiguration(), validJars.toArray());
                } else {
                    dependencies.put(artifact.getConfiguration(), paths.toArray());
                }
            }
        } catch (IOException exception) {
            ManagerLogger.error("Exception thrown while adding jar Dependency: {}", dependency.getName());
            ManagerLogger.error(exception.toString());
            return;
        }

        if (dependencies.size() == 0 && !dependency.getConfigurations().isEmpty()) {
            ManagerLogger.error("Dependency: {}, no libs where found", dependency.getName());
            return;
        }

        ManagerLogger.info("Adding Jar Dependency: {}", dependency.getName());
        for (Map.Entry<String, Object[]> stringEntry : dependencies.entrySet()) {
            ManagerLogger.info("Configuration {}, jars {}", stringEntry.getKey(), Arrays.toString(stringEntry.getValue()));
            getProject().getDependencies().add(stringEntry.getKey(), getProject().getLayout().files(stringEntry.getValue()));
        }
    }

    private void addRepository(Dependency dependency, String repositoryPath, Consumer<RepositoryHandler> repositoryConfiguration) {
        if (dependency.isRegisterDependencyRepositoryToProject()) {
            ManagerLogger.info("Adding {} repository at {} for dependency: {}", dependency.getDependencyType(), repositoryPath, dependency.getName());
            repositoryConfiguration.accept(getProject().getRepositories());
        } else {
            ManagerLogger.info("Skipping registration of {} repository for dependency: {}", dependency.getDependencyType(), dependency.getName());
        }
    }

    private void addRepositoryDependency(Dependency dependency) {
        addRepositoryDependency(dependency, dependency.getPersistentInfo().getProbeData().getArchivesBaseName());
    }

    private void addRepositoryDependency(Dependency dependency, String archivesBaseName) {
        final String[] projectID = dependency.getPersistentInfo().getProbeData().getProjectId().split(":");
        final String dependencyNotation = projectID[0] + ":" + archivesBaseName + ":" + projectID[2];

        for (var artifact : dependency.getConfigurations()) {
            if (artifact.getArtifactProperty().isEmpty()) {
                getProject().getDependencies().add(artifact.getConfiguration(), dependencyNotation, artifact.closure());
                continue;
            }

            for (Artifact.Property property : artifact.getArtifactProperty()) {
                if (!property.include()) {
                    ManagerLogger.info("Dependency: {} Skipping artifact registration {}", dependency.getName(), property.notation());
                    continue;
                }
                if (property.notation().contains(":")) {
                    ManagerLogger.info("Adding Dependency {}, from {}", property.notation(), dependency.getDependencyType());
                    getProject().getDependencies().add(artifact.getConfiguration(), property.notation(), property.closure());
                } else {
                    var notation = projectID[0] + ":" + property.notation() + ":" + projectID[2];

                    ManagerLogger.info("Adding Dependency {}, from {}", notation, dependency.getDependencyType());
                    getProject().getDependencies().add(artifact.getConfiguration(), notation, property.closure());
                }
            }
        }
    }

    private void enableIdeSupport(Dependency dependency) {
        ManagerLogger.info("Dependency: {} enabling ide support", dependency.getName());

        var rootProject = getProject().getRootProject();
        //create source sets of the dependency
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            var sourceSet = rootSourceSetContainer.register(getSourceSetName(dependency, sourceSetData), sourceSetConf -> {
                sourceSetConf.java(dependencySet -> dependencySet.srcDir(sourceSetData.getSources()));
                sourceSetConf.resources(dependencySet -> dependencySet.srcDir(sourceSetData.getResources()));
            }).get();

            for (String task : taskSupplier.apply(sourceSet)) {
                try {
                    rootProject.getTasks().getByName(task).setEnabled(false);
                } catch (UnknownTaskException ignore) {
                }
            }
        }
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            SourceSet depSourceSet = rootSourceSetContainer.getByName(getSourceSetName(dependency, sourceSetData));

            // configure source set classpath
            if (!sourceSetData.getCompileClasspath().isEmpty()) {
                rootProject.getDependencies().add(depSourceSet.getCompileClasspathConfigurationName(),
                        rootProject.getLayout().files(sourceSetData.getCompileClasspath()));
            }

            // link source sets to each other
            if (!sourceSetData.getDependentSourceSets().isEmpty()) {
                for (String dependentSourceSetName : sourceSetData.getDependentSourceSets()) {
                    final Optional<SourceSetData> data = dependency.getPersistentInfo().getProbeData().getSourceSetsData().
                            stream().filter(probe -> probe.getName().equals(dependentSourceSetName)).findFirst();

                    if (data.isPresent()) {
                        final SourceSetData sourceData = data.get();
                        final var set = rootSourceSetContainer.getByName(getSourceSetName(dependency, sourceData));

                        if (!sourceData.getBuildResourcesDir().equals("")) {
                            set.getOutput().setResourcesDir(sourceData.getBuildResourcesDir());
                        }
                        if (set.getOutput().getClassesDirs() instanceof ConfigurableFileCollection configurableFileCollection) {
                            configurableFileCollection.setFrom(sourceData.getBuildClassesDir());

                            depSourceSet.setCompileClasspath(depSourceSet.getCompileClasspath().
                                    plus(set.getOutput()));
                        }
                    }
                }
            }

            //link source sets to the main project using mappers
            for (SourceSetMapper sourceSetMapper : dependency.getSourceSetMappers()) {
                final Set<String> dependentSourceSets = new HashSet<>();
                for (String dependentSourceSetName : sourceSetMapper.getDependencySet()) {
                    if (sourceSetData.getName().equals(dependentSourceSetName)) {
                        dependentSourceSets.add(dependentSourceSetName);
                        if (sourceSetMapper.isRecursive()) {
                            dependentSourceSets.addAll(sourceSetData.getDependentSourceSets());
                        }
                    }
                }

                for (String dependentSourceSetName : dependentSourceSets) {
                    final SourceSet projectSet;
                    if (getProject() == rootProject) {
                        projectSet = rootSourceSetContainer.getByName(sourceSetMapper.getProjectSet());
                    } else {
                        var subProjectSourceSetContainer = getProject().getExtensions().getByType(SourceSetContainer.class);
                        projectSet = subProjectSourceSetContainer.getByName(sourceSetMapper.getProjectSet());
                    }

                    final var sourceData = dependency.getPersistentInfo().getProbeData().getSourceSetsData().
                            stream().filter(probe -> probe.getName().equals(dependentSourceSetName)).findFirst().
                            orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Source set %s not for for dependency %s", dependentSourceSetName, dependency.getName())));

                    final var set = rootSourceSetContainer.getByName(getSourceSetName(dependency, sourceData));
                    if (!sourceData.getBuildResourcesDir().equals("")) {
                        set.getOutput().setResourcesDir(sourceData.getBuildResourcesDir());
                    }
                    if (set.getOutput().getClassesDirs() instanceof ConfigurableFileCollection configurableFileCollection) {
                        configurableFileCollection.setFrom(sourceData.getBuildClassesDir());

                        projectSet.setCompileClasspath(projectSet.getCompileClasspath().
                                plus(set.getOutput()));
                    }
                }
            }
        }
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
