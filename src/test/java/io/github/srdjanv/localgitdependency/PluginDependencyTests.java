package io.github.srdjanv.localgitdependency;

public class PluginDependencyTests {

    /*@TestFactory
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
                clause.getAutomaticCleanup(false);
            });
            dependencyWrapper.setDependencyClosure(builder -> {
                builder.getName(dependencyWrapper.getTestName());
                builder.dependencyType(dependencyType);
                builder.getBuildLauncher(ClosureUtil.<LauncherConfig>configure(launcher -> {
                    launcher.getGradleDaemonMaxIdleTime(0);
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
        final String repo;
        repo = switch (dependencyWrapper.getDependency().getBuildTargets()) {
            case JarFlatDir -> Constants.RepositoryFlatDir.apply(dependencyWrapper.getDependency());
            case MavenLocal -> "MavenLocal";
            case MavenProjectDependencyLocal ->
                    Constants.RepositoryMavenProjectDependencyLocal.apply(dependencyWrapper.getDependency());
            case MavenProjectLocal -> Constants.RepositoryMavenProjectLocal;
            default -> null;
        };

        if (repo != null) {
            long dependencyCount = dependencyWrapper.getProjectManager().getProject().getRepositories().stream()
                    .filter(d -> d.getName().equals(repo)).count();

            Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependencyName() + " repository is not registered wih gradle");
        }

        final long dependencyCount;
        switch (dependencyWrapper.getDependency().getBuildTargets()) {
            case Jar -> {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().size();
            }
            case MavenProjectLocal, MavenProjectDependencyLocal -> {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(dependencyWrapper.getDependency().getName())).count();
            }
            case JarFlatDir, MavenLocal -> {
                dependencyCount = dependencyWrapper.getProjectManager().getProject().getConfigurations().getByName(Constants.JAVA_IMPLEMENTATION)
                        .getDependencies().stream().filter(d -> d.getName().equals(dependencyWrapper.getDependency().getPersistentInfo().getProbeData().getArchivesBaseName())).count();
            }
            default -> throw new IllegalStateException();
        }


        Assertions.assertEquals(1, dependencyCount, () -> dependencyWrapper.getDependency().getName() + " dependency is not registered wih gradle");
    }*/
}
