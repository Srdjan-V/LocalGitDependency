package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GitTest {

    @Test
    void testBranchSwitching() {
        final String testName = "Gradle70To80";

        var dep70 = DependencyRegistry.getTestDependency(
                id -> DependencyRegistry.getGradleBranch("7.0").equals(id));
        configureTest(dep70, testName);
        runTest(dep70, "7.0");

        var dep80 = DependencyRegistry.getTestDependency(
                id -> DependencyRegistry.getGradleBranch("8.0").equals(id));
        configureTest(dep80, testName);
        runTest(dep80, "8.0");
    }

    @Test
    void testCommitSwitching() {
        final String testName = "Commit70To80";

        var dep70 = DependencyRegistry.getTestDependency(
                id -> DependencyRegistry.getGradleBranch("7.0").equals(id));
        var commits70 = DependencyRegistry.getCommitsOfBranch("7.0");
        configureTest(dep70, testName, depConfig -> depConfig.getCommit().set(commits70.get(0)));
        runTest(dep70, "7.0");

        var dep80 = DependencyRegistry.getTestDependency(
                id -> DependencyRegistry.getGradleBranch("8.0").equals(id));
        var commits80 = DependencyRegistry.getCommitsOfBranch("8.0");
        configureTest(dep80, testName, depConfig -> depConfig.getCommit().set(commits80.get(0)));
        runTest(dep80, "8.0");
    }

    @SafeVarargs
    private void configureTest(DependencyWrapper dep, String testName, Action<DependencyConfig>... actions) {
        dep.setTestName(testName);
        dep.applyPluginConfiguration(config -> {
            config.getAutomaticCleanup().set(false);
        });

        Action<DependencyConfig> configAction = config -> {
            config.getName().set(testName);
            config.getKeepGitUpdated().set(true);
        };

        if (actions != null) {
            var actionsC = Arrays.stream(actions).collect(Collectors.toList());
            actionsC.add(0, configAction);
            dep.registerDepToExtension(
                    depConfig -> actionsC.forEach(additionalConfig -> additionalConfig.execute(depConfig)));
        } else dep.registerDepToExtension(configAction);

        dep.registerDepToDependencies(lgdHelper -> lgdHelper.flatDir(testName));
    }

    private void runTest(DependencyWrapper dep, String expectedVersion) {
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
        Assertions.assertEquals(expectedVersion, resolvedDep.get().getVersion());
    }
}
