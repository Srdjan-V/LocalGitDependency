package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.util.dep.DependencyWrapper;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.junit.jupiter.api.Assertions;

public class TestUtil {
    public static void printProjectDependencyData() {
        Project project = Instances.getProject();

        if (!project.getRepositories().isEmpty()) {
            System.out.println(System.lineSeparator());
            System.out.println("Repositories:");
            for (ArtifactRepository repository : project.getRepositories()) {
                if (repository instanceof DefaultMavenArtifactRepository) {
                    DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) repository;

                    System.out.println("=================================================");
                    System.out.println("    " + defaultMavenArtifactRepository.getName());
                    System.out.println("    " + defaultMavenArtifactRepository.getUrl());
                    System.out.println("=================================================");
                    continue;
                }
                System.out.println("=================================================");
                System.out.println("    " + repository.getName());
                System.out.println("=================================================");
            }
        }

        System.out.println(System.lineSeparator());
        System.out.println("Dependencies:");
        project.getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                .getDependencies()
                .forEach(dependency -> {
                    System.out.println("=================================================");
                    System.out.println("    " + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion());
                    System.out.println("=================================================");
                });
    }

    public static void testRegisteredProjectDependencies(DependencyWrapper dependencyWrapper) {
        String repo;
        switch (dependencyWrapper.getDependency().getDependencyType()) {
            case JarFlatDir:
                repo = Constants.RepositoryFlatDir.apply(dependencyWrapper.getDependency().getName());
                break;
            case MavenLocal:
                repo = "MavenLocal";
                break;
            case MavenProjectDependencyLocal:
                repo = Constants.RepositoryMavenProjectDependencyLocal.apply(dependencyWrapper.getDependency().getName());
                break;
            case MavenProjectLocal:
                repo = Constants.RepositoryMavenProjectLocal;
                break;
            default:
                repo = null;
        }

        if (repo != null) {
            final String finalRepo = repo;
            long dependencyCount = Instances.getProject().getRepositories().stream()
                    .filter(d -> d.getName().equals(finalRepo)).count();

            Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependencyName() + " repository is not registered wih gradle");
        }

        long dependencyCount;
        if (dependencyWrapper.getDependency().getDependencyType() == Dependency.Type.Jar) {
            dependencyCount = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies().size();
        } else {
            dependencyCount = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies().stream().filter(d -> d.getName().equals(dependencyWrapper.getDependency().getName())).count();
        }

        Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependency().getName() + " dependency is not registered wih gradle");
    }

}
