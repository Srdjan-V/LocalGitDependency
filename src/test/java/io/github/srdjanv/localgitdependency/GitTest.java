package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GitTest {

    @Test
    void testBranchSwitching() {
        final Consumer<DependencyWrapper> defaultConfig = dep -> {
            final String testName = "Gradle70To80";
            dep.setTestName(testName);
            dep.applyPluginConfiguration(config -> {
                config.getAutomaticCleanup().set(false);
            });
            dep.registerDepToExtension(config -> {
                config.getName().set(testName);
                config.getKeepGitUpdated().set(true);
            });
            dep.registerDepToDependencies(lgdHelper -> lgdHelper.flatDir(testName));
        };
        final BiConsumer<DependencyWrapper, String> test = (dep, version) -> {
            dep.getProjectManager().startPlugin();
            var resolvedDep = dep
                    .getProjectManager()
                    .getProject()
                    .getConfigurations()
                    .getByName(Constants.JAVA_IMPLEMENTATION)
                    .getDependencies()
                    .stream()
                    .findFirst();

            Assertions.assertTrue(resolvedDep.isPresent());
            Assertions.assertEquals(version, resolvedDep.get().getVersion());
        };

        var dep70 = DependencyRegistry.getTestDependency(id -> DependencyRegistry.Types.BRANCH
                .nameType(DependencyRegistry.getGradleBranch("7.0"))
                .equals(id));
        defaultConfig.accept(dep70);
        test.accept(dep70, "7.0");

        var dep80 = DependencyRegistry.getTestDependency(id -> DependencyRegistry.Types.BRANCH
                .nameType(DependencyRegistry.getGradleBranch("8.0"))
                .equals(id));
        defaultConfig.accept(dep80);
        test.accept(dep80, "8.0");
    }
}
