package io.github.srdjanv.localgitdependency.depenency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.PersistentDependencyData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.property.Property;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DependencyManager extends ManagerBase {
    private final Set<Dependency> dependencies = new HashSet<>();

    public DependencyManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {
    }

    public void registerDependency(String configurationName, String dependencyURL, Closure<Property.Builder> configureClosure) {
        Property.Builder dependencyPropertyBuilder = new Property.Builder(dependencyURL);

        if (configureClosure != null) {
            configureClosure.setDelegate(dependencyPropertyBuilder);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
        }

        Property dependencyProperty = new Property(dependencyPropertyBuilder);
        getPropertyManager().applyDefaultProperty(dependencyProperty);

        dependencies.add(new Dependency(configurationName, dependencyProperty));
    }

    public void addBuiltDependencies() {
        boolean addRepositoryMavenProjectLocal = false;
        boolean addRepositoryMavenLocal = false;
        for (Dependency dependency : dependencies) {
            switch (dependency.getDependencyType()) {
                case MavenProjectLocal:
                    addRepositoryMavenProjectLocal = true;
                    break;
                case MavenLocal:
                    addRepositoryMavenLocal = true;
            }
        }

        Project project = getProject();
        if (addRepositoryMavenProjectLocal) addRepositoryMavenProjectLocal(project);
        if (addRepositoryMavenLocal) addRepositoryMavenLocal(project);
        SourceSetContainer sourceSetContainer = project.getRootProject().getExtensions().getByType(SourceSetContainer.class);

        for (Dependency dependency : dependencies) {
            if (!dependency.isRegisterDependencyToProject()) {
                ManagerLogger.info("Skipping dependency registration for {}", dependency.getName());
                continue;
            }
            if (dependency.isAddDependencySourcesToProject()) {
                addSourceSets(sourceSetContainer, dependency);
            }
            switch (dependency.getDependencyType()) {
                case MavenLocal:
                    addMavenJarsAsDependencies(dependency, project);
                    break;
                case MavenProjectLocal:
                    addMavenLocalJarsAsDependencies(dependency, project);
                    break;
                case MavenProjectDependencyLocal:
                    addMavenProjectDependencyLocal(dependency, project);
                    break;
                case JarFlatDir:
                    addJarsAsFlatDirDependencies(dependency, project);
                    break;
                case Jar:
                    addJarsAsDependencies(dependency, project);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private void addRepositoryMavenProjectLocal(Project project) {
        File mavenRepo = Constants.MavenProjectLocal.apply(getPropertyManager().getGlobalProperty().getMavenDir());
        ManagerLogger.info("Adding MavenProjectLocal repository at {}", mavenRepo.getAbsolutePath());
        project.getRepositories().add(project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectLocal);
            mavenArtifactRepository.setUrl(mavenRepo);
        }));
    }

    private void addRepositoryMavenLocal(Project project) {
        ManagerLogger.info("Adding MavenLocal repository");
        project.getRepositories().add(project.getRepositories().mavenLocal());
    }

    private void addMavenJarsAsDependencies(Dependency dependency, Project project) {
        ManagerLogger.info("Adding Dependency: {}, from MavenLocal", dependency.getName());
        addDependencies(project, dependency);
    }

    private void addMavenLocalJarsAsDependencies(Dependency dependency, Project project) {
        ManagerLogger.info("Adding Dependency {}, from MavenProjectLocal", dependency.getName());
        addDependencies(project, dependency);
    }

    private void addMavenProjectDependencyLocal(Dependency dependency, Project project) {
        if (dependency.getMavenFolder() == null) {
            throw new RuntimeException(String.format("Dependency: %s maven folder is null this ideally would not be possible", dependency.getName()));
        }

        String mavenRepo = dependency.getMavenFolder().getAbsolutePath();
        ManagerLogger.info("Adding Dependency: {}, from ProjectDependencyLocal at {}", dependency.getName(), mavenRepo);

        project.getRepositories().add(project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectDependencyLocal.apply(dependency.getName()));
            mavenArtifactRepository.setUrl(mavenRepo);
        }));

        addDependencies(project, dependency);
    }

    private void addJarsAsFlatDirDependencies(Dependency dependency, Project project) {
        Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

        if (!Files.exists(libs)) {
            ManagerLogger.error("Dependency: {}, no libs folder was found", dependency.getName());
            return;
        }

        ManagerLogger.info("Adding FlatDir Dependency: {}", dependency.getName());
        project.getRepositories().add(project.getRepositories().flatDir(flatDir -> {
            flatDir.setName(Constants.RepositoryFlatDir.apply(dependency.getName()));
            flatDir.dir(libs);
        }));
        addDependencies(project, dependency);
    }

    private void addJarsAsDependencies(Dependency dependency, Project project) {
        Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

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

        project.getDependencies().add(dependency.getConfigurationName(), project.getLayout().files(dependencies));
    }

    private void addDependencies(Project project, Dependency dependency) {
        List<String> artifacts = dependency.getGeneratedArtifactNames();
        Closure<?> configureClosure = dependency.getAndClearConfigureClosure();
        if (artifacts != null) {
            String[] projectID = dependency.getPersistentInfo().getProbeData().getProjectId().split(":");
            for (String artifact : artifacts) {
                if (artifact.contains(":")) {
                    project.getDependencies().add(dependency.getConfigurationName(), artifact, configureClosure);
                } else {
                    project.getDependencies().add(dependency.getConfigurationName(), projectID[0] + ":" + artifact + ":" + projectID[2], configureClosure);
                }
            }
            return;
        }
        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getProbeData().getProjectId(), configureClosure);
    }

    private void addSourceSets(SourceSetContainer sourceSetContainer, Dependency dependency) {
        ManagerLogger.info("Dependency: {} adding sourceSets", dependency.getName());

        for (PersistentDependencyData.SourceSetSerializable source : dependency.getPersistentInfo().getProbeData().getSources()) {
            sourceSetContainer.register(dependency.getName() + "-" + source.getName(), sourceSet -> {
                sourceSet.java(dependencySet -> dependencySet.srcDir(source.getSources()));
            });
        }
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }
}
