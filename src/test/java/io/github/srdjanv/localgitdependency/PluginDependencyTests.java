package io.github.srdjanv.localgitdependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectManager;
import io.github.srdjanv.localgitdependency.property.Property;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PluginDependencyTests {

    List<TestWrapper> testWrappers = new ArrayList<>();

    {
        //gradle 7.5
        testWrappers.add(new TestWrapper(
                "TweakedLib",
                "https://github.com/Srdjan-V/TweakedLib.git"));
        //gradle 4.10
        testWrappers.add(new TestWrapper(
                "GroovyScriptFG2",
                "https://github.com/CleanroomMC/GroovyScript.git"));
    }

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
        return testWrappers.stream().
                map(testWrapper -> DynamicTest.dynamicTest(testWrapper.setTestName(dependencyType.name()), () -> {
                    testWrapper.setDependencyType(dependencyType);
                    testWrapper.runTests();
                }));
    }

    static class TestWrapper {
        private final ProjectManager projectManager;
        private final String dependencyName;
        private final String gitUrl;
        private Dependency.Type dependencyType;
        private String testName;

        public TestWrapper(String dependencyName, String gitUrl) {
            projectManager = LocalGitDependencyPlugin.getProject(ProjectInstance.createProject());

            this.dependencyName = dependencyName;
            this.gitUrl = gitUrl;
        }

        public String setTestName(String testName) {
            this.testName = dependencyName + testName;
            return this.testName;
        }

        public void setDependencyType(Dependency.Type dependencyType) {
            this.dependencyType = dependencyType;
        }

        public void runTests() {
            Assertions.assertNotNull(dependencyType, "DependencyType was not set");
            registerDepToExtension();
            initPluginTasks();
            printData();
            assertTest();
        }

        private void registerDepToExtension() {
            Closure<Property.Builder> propertyClosure = new Closure<Property.Builder>(null) {
                public Property.Builder doCall() {
                    Property.Builder builder = (Property.Builder) getDelegate();
                    builder.name(testName);
                    builder.dependencyType(dependencyType);
                    return builder;
                }
            };

            projectManager.getSettingsExtension().add(gitUrl, propertyClosure);
        }

        private void initPluginTasks() {
            projectManager.startPlugin();
        }

        private void printData() {
            Project project = projectManager.getProject();

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

        private void assertTest() {
            String repo;
            switch (dependencyType) {
                case JarFlatDir:
                    repo = Constants.RepositoryFlatDir.apply(testName);
                    break;
                case MavenLocal:
                    repo = "MavenLocal";
                    break;
                case MavenProjectDependencyLocal:
                    repo = Constants.RepositoryMavenProjectDependencyLocal.apply(testName);
                    break;
                case MavenProjectLocal:
                    repo = Constants.RepositoryMavenProjectLocal;
                    break;
                default:
                    repo = null;
            }

            if (repo != null) {
                final String finalRepo = repo;
                long dependency = projectManager.getProject().getRepositories().stream()
                        .filter(d -> d.getName().equals(finalRepo)).count();

                Assertions.assertEquals(1, dependency, () -> dependencyName + " repository is not registered wih gradle");
            }

            long dependency;
            if (dependencyType == Dependency.Type.Jar) {
                dependency = projectManager.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().size();
            } else {
                dependency = projectManager.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(testName)).count();
            }

            dependencyType = null;
            Assertions.assertEquals(1, dependency, () -> testName + " dependency is not registered wih gradle");
        }
    }
}
