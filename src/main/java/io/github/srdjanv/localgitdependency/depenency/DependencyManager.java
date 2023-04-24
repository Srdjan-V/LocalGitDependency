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
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.gradle.api.NamedDomainObjectProvider;
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

        if (configureClosure != null) {
            configureClosure.setDelegate(dependencyPropertyBuilder);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
        }

        DependencyProperty dependencyDependencyProperty = new DependencyProperty(dependencyPropertyBuilder);
        getPropertyManager().applyDefaultProperty(dependencyDependencyProperty);

        dependencies.add(new Dependency(configurationName, dependencyDependencyProperty));
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

        final Object[] dependencies;
        try (Stream<Path> jars = Files.list(libs)) {
            List<String> targetedJars = dependency.getGeneratedJarsToAdd();
            if (targetedJars != null) {
                List<Path> validJars = new ArrayList<>();
                jars.forEach(jar -> {
                    for (String jarName : targetedJars) {
                        if (jar.getFileName().toString().contains(jarName)) {
                            validJars.add(jar);
                        }
                    }
                });
                dependencies = validJars.toArray();
            } else {
                dependencies = jars.toArray();
            }
        } catch (IOException exception) {
            ManagerLogger.error("Exception thrown while adding jar Dependency: {}", dependency.getName());
            ManagerLogger.error(exception.toString());
            return;
        }

        if (dependencies.length == 0) {
            ManagerLogger.error("Dependency: {}, no libs where found", dependency.getName());
            return;
        } else {
            ManagerLogger.info("Adding Jar Dependency: {}", dependency.getName());
            ManagerLogger.info(Arrays.toString(dependencies));
        }

        getProject().getDependencies().add(dependency.getConfigurationName(), getProject().getLayout().files(dependencies));
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
        if (!dependency.isRegisterDependencyToProject()) {
            ManagerLogger.info("Skipping dependency registration for {}", dependency.getName());
            return;
        }

        ManagerLogger.info("Adding Dependency {}, from {}", dependency.getName(), dependency.getDependencyType());

        List<String> artifacts = dependency.getGeneratedArtifactNames();
        Closure<?> configureClosure = dependency.getAndClearConfigureClosure();
        if (artifacts != null) {
            String[] projectID = dependency.getPersistentInfo().getProbeData().getProjectId().split(":");
            for (String artifact : artifacts) {
                if (artifact.contains(":")) {
                    getProject().getDependencies().add(dependency.getConfigurationName(), artifact, configureClosure);
                } else {
                    getProject().getDependencies().add(dependency.getConfigurationName(), projectID[0] + ":" + artifact + ":" + projectID[2], configureClosure);
                }
            }
            return;
        }
        getProject().getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getProbeData().getProjectId(), configureClosure);
    }

    // TODO: 21/03/2023 improve
    private void enableIdeSupport(SourceSetContainer sourceSetContainer, Dependency dependency) {
        ManagerLogger.info("Dependency: {} enabling ide support", dependency.getName());

        var project = getProject().getRootProject();
        for (SourceSetData source : dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            NamedDomainObjectProvider<SourceSet> sourceSetNamedDomainObjectProvider = sourceSetContainer.register(dependency.getName() + "-" + source.getName(), sourceSet -> {
                sourceSet.java(dependencySet -> dependencySet.srcDir(source.getSources()));
            });

            SourceSet sourceSet = sourceSetNamedDomainObjectProvider.get();
            for (String classpathDependency : source.getRepositoryClasspathDependencies()) {
                project.getDependencies().add(sourceSet.getCompileClasspathConfigurationName(), classpathDependency);
            }

            if (!source.getFileClasspathDependencies().isEmpty()) {
                project.getDependencies().add(sourceSet.getCompileClasspathConfigurationName(), project.getLayout().files(source.getFileClasspathDependencies()));
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

    @Override
    @Unmodifiable
    public Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
