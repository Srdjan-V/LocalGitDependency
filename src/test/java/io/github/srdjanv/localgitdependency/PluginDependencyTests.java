package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

public class PluginDependencyTests {

    @TestFactory
    Stream<DynamicTest> TestMavenLocal() {
        return createTestStream(Dependency.Type.MavenLocal);
    }

    @TestFactory
    Stream<DynamicTest> TestMavenProjectLocal() {
        return createTestStream(Dependency.Type.MavenProjectLocal);
    }

    @TestFactory
    Stream<DynamicTest> TestMavenProjectDependencyLocal() {
        return createTestStream(Dependency.Type.MavenProjectDependencyLocal);
    }

    @TestFactory
    Stream<DynamicTest> TestJarFlatDir() {
        return createTestStream(Dependency.Type.JarFlatDir);
    }

    @TestFactory
    Stream<DynamicTest> TestJar() {
        return createTestStream(Dependency.Type.Jar);
    }

    private Stream<DynamicTest> createTestStream(final Dependency.Type dependencyType) {
        List<DependencyWrapper> dependencyWrappers = DependencyRegistry.getTestDependencies();

        dependencyWrappers.forEach(dependencyWrapper -> {
            dependencyWrapper.setTestName(dependencyType.name());
            dependencyWrapper.setPluginClosure(clause -> {
                clause.automaticCleanup(false);
            });
            dependencyWrapper.setDependencyClosure(builder -> {
                builder.name(dependencyWrapper.getTestName());
                builder.dependencyType(dependencyType);
                builder.buildLauncher(ClosureUtil.<LauncherBuilder>configure(launcher -> {
                    launcher.gradleDaemonMaxIdleTime(0);
                }));
                builder.configuration(Constants.JAVA_IMPLEMENTATION);
            });
            dependencyWrapper.setTest(test -> {
                printData(dependencyWrapper.getProjectManager().getProject());
                assertTest(dependencyWrapper);
            });
        });

        return dependencyWrappers.stream().
                map(testWrapper -> DynamicTest.dynamicTest(testWrapper.getTestName(), testWrapper::startPluginAndRunTests));
    }

    public static void printData(Project project) {

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

    public static void assertTest(DependencyWrapper dependencyWrapper) {
        String repo;
        switch (dependencyWrapper.getDependency().getDependencyType()) {
            case JarFlatDir:
                repo = Constants.RepositoryFlatDir.apply(dependencyWrapper.getDependency());
                break;
            case MavenLocal:
                repo = "MavenLocal";
                break;
            case MavenProjectDependencyLocal:
                repo = Constants.RepositoryMavenProjectDependencyLocal.apply(dependencyWrapper.getDependency());
                break;
            case MavenProjectLocal:
                repo = Constants.RepositoryMavenProjectLocal;
                break;
            default:
                repo = null;
        }

        if (repo != null) {
            final String finalRepo = repo;
            long dependencyCount = dependencyWrapper.getProjectManager().getProject().getRepositories().stream()
                    .filter(d -> d.getName().equals(finalRepo)).count();

            Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependencyName() + " repository is not registered wih gradle");
        }

        long dependencyCount;
        switch (dependencyWrapper.getDependency().getDependencyType()) {
            case Jar: {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().size();
                break;
            }

            case MavenProjectLocal:
            case MavenProjectDependencyLocal: {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(dependencyWrapper.getDependency().getName())).count();
                break;
            }
            case JarFlatDir:
            case MavenLocal: {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(dependencyWrapper.getDependency().getPersistentInfo().getProbeData().getArchivesBaseName())).count();
                break;
            }
            default:
                throw new IllegalStateException();
        }


        Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependency().getName() + " dependency is not registered wih gradle");
    }
}
