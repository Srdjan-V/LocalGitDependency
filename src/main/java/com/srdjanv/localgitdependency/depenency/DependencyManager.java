package com.srdjanv.localgitdependency.depenency;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.property.Property;
import groovy.lang.Closure;
import org.gradle.api.Project;
import com.srdjanv.localgitdependency.Instances;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();

    public void registerDependency(String configurationName, String dependencyURL, Closure<Property.Builder> configureClosure) {
        Property.Builder dependencyProperty = new Property.Builder(dependencyURL);

        if (configureClosure != null) {
            configureClosure.setDelegate(dependencyProperty);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
        }

        dependencies.add(new Dependency(configurationName, new Property(dependencyProperty)));
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

        Project project = Instances.getProject();
        if (addRepositoryMavenProjectLocal) addRepositoryMavenProjectLocal(project);
        if (addRepositoryMavenLocal) addRepositoryMavenLocal(project);

        for (Dependency dependency : dependencies) {
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
            }
        }
    }

    private void addRepositoryMavenProjectLocal(Project project) {
        File mavenRepo = Constants.MavenProjectLocal.apply(Instances.getPropertyManager().getGlobalProperty().getMavenFolder());
        Logger.info("Adding MavenProjectLocal repository at {}", mavenRepo.getAbsolutePath());
        project.getRepositories().add(project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectLocal);
            mavenArtifactRepository.setUrl(mavenRepo);
        }));
    }

    private void addRepositoryMavenLocal(Project project) {
        Logger.info("Adding MavenLocal repository");
        project.getRepositories().add(project.getRepositories().mavenLocal());
    }

    private void addMavenJarsAsDependencies(Dependency dependency, Project project) {
        Logger.info("Adding Dependency {}, from MavenLocal", dependency.getName());
        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getProjectId());
    }


    private void addMavenLocalJarsAsDependencies(Dependency dependency, Project project) {
        Logger.info("Adding Dependency {}, from MavenProjectLocal", dependency.getName());
        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getProjectId());
    }

    private void addMavenProjectDependencyLocal(Dependency dependency, Project project) {
        if (dependency.getMavenFolder() == null) {
            throw new RuntimeException(String.format("Dependency %s maven folder is null this ideally would not be possible", dependency.getName()));
        }

        String mavenRepo = dependency.getMavenFolder().getAbsolutePath();
        Logger.warn("Adding Dependency {}, from ProjectDependencyLocal at {}", dependency.getName(), mavenRepo);

        project.getRepositories().add(project.getRepositories().maven(mavenArtifactRepository -> {
            mavenArtifactRepository.setName(Constants.RepositoryMavenProjectDependencyLocal.apply(dependency.getName()));
            mavenArtifactRepository.setUrl(mavenRepo);
        }));

        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getProjectId());
    }

    private void addJarsAsFlatDirDependencies(Dependency dependency, Project project) {
        Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

        if (!Files.exists(libs)) {
            Logger.error("Dependency {}, no libs folder was found", dependency.getName());
            return;
        }

        Logger.info("Adding Dependency {}", dependency.getName());
        project.getRepositories().add(project.getRepositories().flatDir(flatDir -> {
            flatDir.setName(Constants.RepositoryFlatDir.apply(dependency.getName()));
            flatDir.dir(libs);
        }));
        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getProjectId());
    }

    private void addJarsAsDependencies(Dependency dependency, Project project) {
        Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

        if (!Files.exists(libs)) {
            Logger.error("Dependency {}, no libs folder was found", dependency.getName());
            return;
        }

        Object[] dependencies;
        try (Stream<Path> jars = Files.list(libs)) {
            dependencies = jars.toArray();
        } catch (IOException exception) {
            Logger.error("Exception thrown while building Dependency {}", dependency.getName());
            Logger.error(exception.toString());
            return;
        }

        if (dependencies.length == 0) {
            Logger.error("Dependency {}, no libs where found", dependency.getName());
            return;
        } else {
            Logger.info("Adding Dependency {}, and its jars", dependency.getName());
            Logger.info(Arrays.toString(dependencies));
        }

        project.getDependencies().add(dependency.getConfigurationName(), project.getLayout().files(dependencies));
    }


    public Set<Dependency> getDependencies() {
        return dependencies;
    }
}
