package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir.IFlatDirRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy.IIvyRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven.IMavenRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.impl.Artifact;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.SourceSetMapper;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DependencyManager extends ManagerBase implements IDependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();

    DependencyManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public void registerDependency(String configurationName, String dependencyURL, @SuppressWarnings("rawtypes") Closure configureClosure) {
        DependencyProperty.Builder dependencyPropertyBuilder = new DependencyProperty.Builder(dependencyURL);
        dependencyPropertyBuilder.configuration(configurationName);

        if (configureClosure != null) {
            ClosureUtil.delegate(configureClosure, dependencyPropertyBuilder);
        }

        DependencyProperty dependencyProperty = new DependencyProperty(dependencyPropertyBuilder);
        getPropertyManager().applyDefaultProperty(dependencyProperty);

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
        SourceSetContainer sourceSetContainer = getProject().getRootProject().getExtensions().getByType(SourceSetContainer.class);

        for (Dependency dependency : dependencies) {
            if (dependency.isEnableIdeSupport()) {
                enableIdeSupport(sourceSetContainer, dependency);
            }
            switch (dependency.getDependencyType()) {
                case MavenLocal -> {
                    addMavenJarsAsDependencies(dependency);
                    addRepositoryMavenLocal = true;
                }
                case MavenProjectLocal -> {
                    addMavenLocalJarsAsDependencies(dependency);
                    addRepositoryMavenProjectLocal = true;
                }
                case MavenProjectDependencyLocal -> addMavenProjectDependencyLocal(dependency);
                case JarFlatDir -> addJarsAsFlatDirDependencies(dependency);
                case Jar -> addJarsAsDependencies(dependency);
                default -> throw new IllegalStateException();
            }
        }

        if (addRepositoryMavenLocal) addRepositoryMavenLocal();
        if (addRepositoryMavenProjectLocal) addRepositoryMavenProjectLocal();
    }

    private void addRepositoryMavenProjectLocal() {
        final File mavenRepo = Constants.MavenProjectLocal.apply(getPropertyManager().getGlobalProperty().getMavenDir());
        ManagerLogger.info("Adding MavenProjectLocal repository at {}", mavenRepo.getAbsolutePath());
        getProject().getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectLocal);
            mavenArtifactRepository.setUrl(mavenRepo);
        });
    }

    private void addRepositoryMavenLocal() {
        ManagerLogger.info("Adding MavenLocal repository");
        getProject().getRepositories().mavenLocal();
    }

    private void addMavenJarsAsDependencies(Dependency dependency) {
        addRepositoryDependency(dependency);
    }

    private void addMavenLocalJarsAsDependencies(Dependency dependency) {
        addRepositoryDependency(dependency);
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
        addRepositoryDependency(dependency);
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
        addRepositoryDependency(dependency);
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
                    dependencies.put(artifact.getConfiguration(), jars.toArray());
                }
            }
        } catch (IOException exception) {
            ManagerLogger.error("Exception thrown while adding jar Dependency: {}", dependency.getName());
            ManagerLogger.error(exception.toString());
            return;
        }

        if (dependencies.size() == 0) {
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
        String[] projectID = dependency.getPersistentInfo().getProbeData().getProjectId().split(":");
        var configurations = dependency.getConfigurations();
        for (var artifact : configurations) {
            if (artifact.getArtifactProperty().isEmpty()) {
                getProject().getDependencies().add(artifact.getConfiguration(), dependency.getPersistentInfo().getProbeData().getProjectId());
                continue;
            }

            for (Artifact.Property property : artifact.getArtifactProperty()) {
                if (!property.include()) {
                    ManagerLogger.info("Skipping dependency registration for {}", dependency.getName());
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

    // TODO: 21/03/2023 improve
    private void enableIdeSupport(SourceSetContainer sourceSetContainer, Dependency dependency) {
        ManagerLogger.info("Dependency: {} enabling ide support", dependency.getName());

        var project = getProject().getRootProject();
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            sourceSetContainer.register(getSourceSetName(dependency, sourceSetData), sourceSetConf -> {
                sourceSetConf.java(dependencySet -> dependencySet.srcDir(sourceSetData.getSources()));
                sourceSetConf.resources(dependencySet -> dependencySet.srcDir(sourceSetData.getResources()));
            });
        }
        for (SourceSetData sourceSetData : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            SourceSet sourceSet = sourceSetContainer.getByName(getSourceSetName(dependency, sourceSetData));

            if (!sourceSetData.getCompileClasspath().isEmpty()) {
                project.getDependencies().add(sourceSet.getCompileClasspathConfigurationName(),
                        project.getLayout().files(sourceSetData.getCompileClasspath()));
            }

            if (!sourceSetData.getDependentSourceSets().isEmpty()) {
                var depSets = new ArrayList<SourceSet>();

                for (String dependentSourceSetName : sourceSetData.getDependentSourceSets()) {
                    for (SourceSetData data : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
                        if (data.getName().equals(dependentSourceSetName)) {
                            depSets.add(sourceSetContainer.getByName(getSourceSetName(dependency, dependentSourceSetName)));
                        }
                    }
                }
                //sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(project.getLayout().files(path)));
                for (SourceSet depSet : depSets) {
                    sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(depSet.getOutput()));
                }
                for (SourceSetMapper sourceSetMapper : dependency.getSourceSetMappers()) {
                    for (String s : sourceSetMapper.getDependencySet()) {
                        if (sourceSetData.getName().equals(s)) {
                            var projectSet = sourceSetContainer.getByName(sourceSetMapper.getProjectSet());

                            for (SourceSet depSet : depSets) {
                                projectSet.setCompileClasspath(projectSet.getCompileClasspath().plus(depSet.getOutput()));
                            }

                            /*projectSet.java(java -> {
                                java.srcDir(pathSources);
                            });
                            projectSet.resources(java -> {
                                java.srcDir(pathResources);
                            });*/
                        }
                    }
                }
            }
        }

        var repoHandler = getProject().getRepositories();
        for (Repository repository : dependency.getPersistentInfo().getProbeData().getRepositoryList()) {
            switch (repository.getType()) {
                case Constants.Maven -> {
                    var maven = (IMavenRepository) repository;
                    if (maven.getAuthenticated()) {
                        ManagerLogger.info("Repository {} is authenticated skipping registration", maven.getName());
                        return;
                    }
                    repoHandler.maven(maven.configureAction());
                }

                case Constants.FlatDir -> {
                    var flatDir = (IFlatDirRepository) repository;
                    if (flatDir.getDirs().isEmpty()) {
                        return;
                    }
                    repoHandler.flatDir(flatDir.configureAction());
                }

                case Constants.Ivy -> {
                    var ivy = (IIvyRepository) repository;
                    if (ivy.getAuthenticated()) {
                        ManagerLogger.info("Repository {} is authenticated skipping registration", ivy.getName());
                        return;
                    }
                    repoHandler.ivy(ivy.configureAction());
                }
            }
        }
    }

    private String getSourceSetName(Dependency dependency, SourceSetData sourceSetData) {
        return dependency.getName() + "-" + sourceSetData.getName();
    }

    private String getSourceSetName(Dependency dependency, String name) {
        return dependency.getName() + "-" + name;
    }

    @Override
    @Unmodifiable
    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
