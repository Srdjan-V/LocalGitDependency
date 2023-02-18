package srki2k.localgitdependency;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.property.Property;

public class PluginDependencyTests {

    @Test
    void TestMavenLocal() {
        ProjectInstance.createProject();

        Wrapper wrapper = new Wrapper("TweakedLibTestMavenLocal", Instances.getSettingsExtension().MavenLocal());
        Closure<Property.Builder> closure = builderClosure(wrapper);
        Instances.getSettingsExtension().add("https://github.com/Srdjan-V/TweakedLib.git", closure);

        commonInit();
        printData();
        test(wrapper);
    }

    @Test
    void TestMavenProjectLocal() {
        ProjectInstance.createProject();

        Wrapper wrapper = new Wrapper("TweakedLibTestMavenProjectLocal", Instances.getSettingsExtension().MavenProjectLocal());
        Closure<Property.Builder> closure = builderClosure(wrapper);
        Instances.getSettingsExtension().add("https://github.com/Srdjan-V/TweakedLib.git", closure);

        commonInit();
        printData();
        test(wrapper);
    }

    @Test
    void TestMavenProjectDependencyLocal() {
        ProjectInstance.createProject();

        Wrapper wrapper = new Wrapper("TweakedLibMavenProjectDependencyLocal", Instances.getSettingsExtension().MavenProjectDependencyLocal());
        Closure<Property.Builder> closure = builderClosure(wrapper);
        Instances.getSettingsExtension().add("https://github.com/Srdjan-V/TweakedLib.git", closure);

        commonInit();
        printData();
        test(wrapper);
    }

    @Test
    void TestJarFlatDir() {
        ProjectInstance.createProject();

        Wrapper wrapper = new Wrapper("TweakedLibJarFlatDir", Instances.getSettingsExtension().JarFlatDir());
        Closure<Property.Builder> closure = builderClosure(wrapper);
        Instances.getSettingsExtension().add("https://github.com/Srdjan-V/TweakedLib.git", closure);

        commonInit();
        printData();
        test(wrapper);
    }

    @Test
    void TestJar() {
        ProjectInstance.createProject();

        Wrapper wrapper = new Wrapper("TweakedLibJar", Instances.getSettingsExtension().Jar());
        Closure<Property.Builder> closure = builderClosure(wrapper);
        Instances.getSettingsExtension().add("https://github.com/Srdjan-V/TweakedLib.git", closure);

        commonInit();
        printData();
        test(wrapper);
    }

    static Closure<Property.Builder> builderClosure(Wrapper wrapper) {
        return new Closure<Property.Builder>(null) {
            public Property.Builder doCall() {
                Property.Builder builder = (Property.Builder) getDelegate();
                builder.name(wrapper.getName());
                builder.dependencyType(wrapper.getDependencyType());
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

    static void test(Wrapper wrapper) {
        String repo;
        switch (wrapper.getDependencyType()) {
            case JarFlatDir:
                repo = Constants.RepositoryFlatDir.apply(wrapper.getName());
                break;
            case MavenLocal:
                repo = "MavenLocal";
                break;
            case MavenProjectDependencyLocal:
                repo = Constants.RepositoryMavenProjectDependencyLocal.apply(wrapper.getName());
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

            Assertions.assertEquals(1, dependency, () -> wrapper.getName() + " repository is not registered wih gradle");
        }

        long dependency;
        if (wrapper.getDependencyType() == Dependency.Type.Jar) {
            dependency = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies().size();
        } else {
            dependency = Instances.getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies().stream().filter(d -> d.getName().equals(wrapper.name)).count();
        }

        Assertions.assertEquals(1, dependency, () -> wrapper.getName() + " dependency is not registered wih gradle");
    }

    static class Wrapper {
        private final String name;
        private final Dependency.Type dependencyType;

        public Wrapper(String name, Dependency.Type dependencyType) {
            this.name = name;
            this.dependencyType = dependencyType;
        }

        public String getName() {
            return name;
        }

        public Dependency.Type getDependencyType() {
            return dependencyType;
        }
    }

}
