package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.util.dep.DependencyRegistry;
import io.github.srdjanv.localgitdependency.util.dep.DependencyWrapper;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.util.TestUtil;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

public class BasicPluginTests {

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
            dependencyWrapper.setDependencyClosure(builder -> builder.dependencyType(dependencyType));
            dependencyWrapper.setTest(test -> {
                TestUtil.printProjectDependencyData();
                TestUtil.testRegisteredProjectDependencies(dependencyWrapper);
            });
        });

        return dependencyWrappers.stream().
                map(testWrapper -> DynamicTest.dynamicTest(testWrapper.getTestName(), testWrapper::startPluginAndRunTests));
    }

}
