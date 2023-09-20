package io.github.srdjanv.localgitdependency;

import static io.github.srdjanv.localgitdependency.depenency.Dependency.Type.*;
import static io.github.srdjanv.localgitdependency.depenency.Dependency.Type.JarFlatDir;

import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import io.github.srdjanv.localgitdependency.depenency.Dependency;

import java.util.Collections;
import java.util.stream.Stream;

import io.github.srdjanv.localgitdependency.project.BuildScriptGenerator;
import org.apache.tools.ant.taskdefs.Java;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

public class PluginDependencyTests {

    @TestFactory
    Stream<DynamicTest> TestMavenLocal() {
        return createTestStream(MavenLocal);
    }

    @TestFactory
    Stream<DynamicTest> TestJarFlatDir() {
        return createTestStream(JarFlatDir);
    }

    @TestFactory
    Stream<DynamicTest> TestJar() {
        return createTestStream(Jar);
    }

    private Stream<DynamicTest> createTestStream(final Dependency.Type dependencyType) {
        var testDependencies = DependencyRegistry.getAllTestDependencies();

        testDependencies.forEach(wrapper -> {
            wrapper.setTestName(dependencyType.name());
            wrapper.applyPluginConfiguration(config -> {
                config.getAutomaticCleanup().set(false);
            });
            wrapper.registerDepToExtension(config -> {
                config.getName().set(wrapper.getTestName());
            });
            wrapper.registerDepToDependencies(lgdHelper -> switch (dependencyType) {
                case MavenLocal -> lgdHelper.mavenLocal(wrapper.getTestName());
                case JarFlatDir -> lgdHelper.flatDir(wrapper.getTestName());
                case Jar -> lgdHelper.jar(wrapper.getTestName());
                default -> throw new IllegalStateException("Unexpected value: " + dependencyType);
            });
        });

        return testDependencies.stream().map(testWrapper ->
                DynamicTest.dynamicTest(testWrapper.getTestName(), () -> {
                    testWrapper.getProjectManager().startPlugin();
                    printData(testWrapper.getProjectManager().getProject());
                    assertTest(testWrapper);
                }));
    }

    @TestFactory
    Stream<DynamicTest> TestSubDepMavenLocal() {
        return runSubDepTest(MavenLocal);
    }

    @TestFactory
    Stream<DynamicTest> TestSubDepJarFlatDir() {
        return runSubDepTest(JarFlatDir);
    }

    @TestFactory
    Stream<DynamicTest> TestSubDepJar() {
        return runSubDepTest(Jar);
    }

    private Stream<DynamicTest> runSubDepTest(Dependency.Type type) {
        // only gradle 8.0 is working with lgd sub deps tests
        var wrapper = DependencyRegistry.getTestDependency(id -> DependencyRegistry.getGradleBranch("8.0").equals(id));

        final var subDepName = "SubDep";
        wrapper.setTestName(type.name());
        BuildScriptGenerator.generate(wrapper, new BuildScriptGenerator.LDGDeps().append(String.format(
                """                                     
                           register("https://github.com/Srdjan-V/LocalGitDependencyTestRepo.git") {
                               branch = "%s"
                               name = "%s"
                           }
                        """, wrapper.getBranch(), subDepName)));

        wrapper.applyPluginConfiguration(config -> {
           config.getAutomaticCleanup().set(false);
        });

        wrapper.registerDepToExtension(config -> {
            config.getName().set(wrapper.getTestName());
            config.getKeepGitUpdated().set(false);
        });

        wrapper.registerDepToDependencies(lgdHelper -> switch (type) {
            case MavenLocal -> lgdHelper.mavenLocal(wrapper.getTestName() + "." + subDepName);
            case JarFlatDir -> lgdHelper.flatDir(wrapper.getTestName() + "." + subDepName);
            case Jar -> lgdHelper.jar(wrapper.getTestName() + "." + subDepName);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        });

        return Stream.of(wrapper).map(testWrapper ->
                DynamicTest.dynamicTest(testWrapper.getTestName(), () -> {
                    testWrapper.getProjectManager().startPlugin();
                    printData(testWrapper.getProjectManager().getProject());
                    assertTest(testWrapper);
                }));
    }

    public static void printData(Project project) {
        if (!project.getRepositories().isEmpty()) {
            System.out.println();
            System.out.println("Repositories:");
            for (ArtifactRepository repository : project.getRepositories()) {
                if (repository instanceof DefaultMavenArtifactRepository defaultMavenArtifactRepository) {
                    System.out.println("=================================================");
                    System.out.println(Constants.TAB_INDENT + defaultMavenArtifactRepository.getName());
                    System.out.println(Constants.TAB_INDENT + defaultMavenArtifactRepository.getUrl());
                    System.out.println("=================================================");
                    continue;
                }
                System.out.println("=================================================");
                System.out.println(Constants.TAB_INDENT + repository.getName());
                System.out.println("=================================================");
            }
        }

        System.out.println();
        System.out.println("Dependencies:");
        project.getConfigurations()
                .getByName(Constants.JAVA_IMPLEMENTATION)
                .getDependencies()
                .forEach(dependency -> {
                    System.out.println("=================================================");
                    System.out.println(Constants.TAB_INDENT + dependency.getGroup() + ":" + dependency.getName() + ":"
                            + dependency.getVersion());
                    System.out.println("=================================================");
                });
    }

    public static void assertTest(DependencyWrapper dependencyWrapper) {
        final long dependencyCount;
        var tags = dependencyWrapper.getDependency().getBuildTags();

        if (tags.contains(Jar)) {
            dependencyCount = dependencyWrapper
                    .getProjectManager()
                    .getProject()
                    .getConfigurations()
                    .getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies()
                    .size();
        } else if (tags.contains(JarFlatDir) || tags.contains(MavenLocal)) {
            long repoCount = dependencyWrapper
                    .getProjectManager()
                    .getProject()
                    .getRepositories()
                    .size();
            Assertions.assertEquals(
                    1,
                    repoCount,
                    () -> dependencyWrapper.getDependency().getName() + " repository is not registered wih gradle");

            dependencyCount = dependencyWrapper
                    .getProjectManager()
                    .getProject()
                    .getConfigurations()
                    .getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies()
                    .stream()
                    .filter(d -> d.getName()
                            .equals(dependencyWrapper
                                    .getDependency()
                                    .getPersistentInfo()
                                    .getProbeData()
                                    .getArchivesBaseName()))
                    .count();
        } else dependencyCount = 0;

        Assertions.assertEquals(
                1,
                dependencyCount,
                () -> dependencyWrapper.getDependency().getName() + " dependency is not registered wih gradle");
    }
}
