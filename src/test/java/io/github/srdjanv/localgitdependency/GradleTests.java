package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.project.BuildScriptGenerator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class GradleTests {
    @TestFactory
    Stream<DynamicTest> GradleVersionTests() {
        final var wrappers = DependencyRegistry.getAllTestDependencies();

        final var testName = "VersionTest";
        wrappers.forEach(wrapper -> {
            wrapper.setTestName(testName);
            BuildScriptGenerator.generate(
                    wrapper,
                    new BuildScriptGenerator.LDGDeps()
                            .registerDep(
                                    wrapper.getBranch(), wrapper.getBranch().replace(".", "")));

            wrapper.registerDepToExtension(config -> {
                config.getName().set(wrapper.getTestName());
                config.getKeepGitUpdated().set(false);
            });
        });

        return wrappers.stream()
                .map(wrapper -> DynamicTest.dynamicTest(wrapper.getTestName(), () -> {
                    wrapper.getProjectManager().startPlugin();
                    Assertions.assertEquals(
                            1,
                            wrapper.getProjectManager()
                                    .getDependencyManager()
                                    .getDependencies()
                                    .size());
                }));
    }
}
