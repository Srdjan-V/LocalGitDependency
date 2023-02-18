package srki2k.localgitdependency;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.property.Property;

import java.util.ArrayList;
import java.util.List;

public class PluginDependencyTests {

    List<GitWrapper> gitWrappers = new ArrayList<>();

    {
        //gradle 7.5
        gitWrappers.add(new GitWrapper(
                "TweakedLib",
                "https://github.com/Srdjan-V/TweakedLib.git"));
        //gradle 4.10
        gitWrappers.add(new GitWrapper(
                "GroovyScriptFG2",
                "https://github.com/CleanroomMC/GroovyScript.git"));
    }

    @Test
    void TestMavenLocal() {
        for (GitWrapper gitWrapper : gitWrappers) {
            ProjectInstance.createProject();

            TestWrapper testWrapper = new TestWrapper(gitWrapper.getName() + "MavenLocal", Instances.getSettingsExtension().MavenLocal());
            Closure<Property.Builder> closure = builderClosure(testWrapper);
            Instances.getSettingsExtension().add(gitWrapper.getGitUrl(), closure);

            commonInit();
            printData();
            testWrapper.test();
        }
    }

    @Test
    void TestMavenProjectLocal() {
        for (GitWrapper gitWrapper : gitWrappers) {
            ProjectInstance.createProject();

            TestWrapper testWrapper = new TestWrapper(gitWrapper.getName() + "MavenProjectLocal", Instances.getSettingsExtension().MavenProjectLocal());
            Closure<Property.Builder> closure = builderClosure(testWrapper);
            Instances.getSettingsExtension().add(gitWrapper.getGitUrl(), closure);

            commonInit();
            printData();
            testWrapper.test();
        }
    }

    @Test
    void TestMavenProjectDependencyLocal() {
        for (GitWrapper gitWrapper : gitWrappers) {
            ProjectInstance.createProject();

            TestWrapper testWrapper = new TestWrapper(gitWrapper.getName() + "MavenProjectDependencyLocal", Instances.getSettingsExtension().MavenProjectDependencyLocal());
            Closure<Property.Builder> closure = builderClosure(testWrapper);
            Instances.getSettingsExtension().add(gitWrapper.getGitUrl(), closure);

            commonInit();
            printData();
            testWrapper.test();
        }
    }

    @Test
    void TestJarFlatDir() {
        for (GitWrapper gitWrapper : gitWrappers) {
            ProjectInstance.createProject();

            TestWrapper testWrapper = new TestWrapper(gitWrapper.getName() + "JarFlatDir", Instances.getSettingsExtension().JarFlatDir());
            Closure<Property.Builder> closure = builderClosure(testWrapper);
            Instances.getSettingsExtension().add(gitWrapper.getGitUrl(), closure);

            commonInit();
            printData();
            testWrapper.test();
        }
    }

    @Test
    void TestJar() {
        for (GitWrapper gitWrapper : gitWrappers) {
            ProjectInstance.createProject();

            TestWrapper testWrapper = new TestWrapper(gitWrapper.getName() + "Jar", Instances.getSettingsExtension().Jar());
            Closure<Property.Builder> closure = builderClosure(testWrapper);
            Instances.getSettingsExtension().add(gitWrapper.getGitUrl(), closure);

            commonInit();
            printData();
            testWrapper.test();
        }
    }

    static Closure<Property.Builder> builderClosure(TestWrapper testWrapper) {
        return new Closure<Property.Builder>(null) {
            public Property.Builder doCall() {
                Property.Builder builder = (Property.Builder) getDelegate();
                builder.name(testWrapper.getName());
                builder.dependencyType(testWrapper.getDependencyType());
                return builder;
            }
        };
    }

    static void commonInit() {
        Instances.getGitManager().initRepos();
        Instances.getGradleManager().initGradleAPI();
        Instances.getPersistenceManager().savePersistentData();
        Instances.getGradleManager().buildDependencies();
        Instances.getDependencyManager().addBuiltDependencies();
    }

    static void printData() {
        Project project = Instances.getProject();

        if (!project.getRepositories().isEmpty()) {
            System.out.println(System.lineSeparator());
            System.out.println("Repositories:");
        }
        for (ArtifactRepository repository : project.getRepositories()) {
            if (repository instanceof DefaultMavenArtifactRepository) {
                DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) repository;

                System.out.println("=================================================");
                System.out.println(defaultMavenArtifactRepository.getName());
                System.out.println(defaultMavenArtifactRepository.getUrl());
                System.out.println("=================================================");
                continue;
            }
            System.out.println("=================================================");
            System.out.println(repository.getName());
            System.out.println("=================================================");
        }

        System.out.println(System.lineSeparator());
        System.out.println("Dependencies:");
        project.getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                .getDependencies()
                .forEach(dependency -> {
                    System.out.println("=================================================");
                    System.out.println(dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion());
                    System.out.println("=================================================");
                });
    }

    static class TestWrapper {
        private final String name;
        private final Dependency.Type dependencyType;

        public TestWrapper(String name, Dependency.Type dependencyType) {
            this.name = name;
            this.dependencyType = dependencyType;
        }

        public String getName() {
            return name;
        }

        public Dependency.Type getDependencyType() {
            return dependencyType;
        }

        public void test() {
            String repo;
            switch (getDependencyType()) {
                case JarFlatDir:
                    repo = Constants.RepositoryFlatDir.apply(getName());
                    break;
                case MavenLocal:
                    repo = "MavenLocal";
                    break;
                case MavenProjectDependencyLocal:
                    repo = Constants.RepositoryMavenProjectDependencyLocal.apply(getName());
                    break;
                case MavenProjectLocal:
                    repo = Constants.RepositoryMavenProjectLocal;
                    break;
                default:
                    repo = null;
            }

            if (repo != null) {
                final String finalRepo = repo;
                long dependency = Instances.getProject().getRepositories().stream()
                        .filter(d -> d.getName().equals(finalRepo)).count();

                Assertions.assertEquals(1, dependency, () -> getName() + " repository is not registered wih gradle");
            }

            long dependency;
            if (getDependencyType() == Dependency.Type.Jar) {
                dependency = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().size();
            } else {
                dependency = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(getName())).count();
            }

            Assertions.assertEquals(1, dependency, () -> getName() + " dependency is not registered wih gradle");
        }
    }

    static class GitWrapper {
        private final String name;
        private final String gitUrl;

        public GitWrapper(String name, String gitUrl) {
            this.name = name;
            this.gitUrl = gitUrl;
        }

        public String getName() {
            return name;
        }

        public String getGitUrl() {
            return gitUrl;
        }
    }
}
