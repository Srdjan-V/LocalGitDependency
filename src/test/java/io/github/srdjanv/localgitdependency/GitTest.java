package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.dependency.DependencyWrapper;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class GitTest {

    @Test
    void test() {
        DependencyWrapper dependencyWrapper = DependencyRegistry.getTestDependencies().stream().findFirst().get();

        dependencyWrapper.setTestName("GitTest");
        dependencyWrapper.setPluginClosure(clause -> {
            clause.automaticCleanup(false);
        });
        dependencyWrapper.setDependencyClosure(builder -> {
            builder.name(dependencyWrapper.getTestName());
            builder.buildLauncher(ClosureUtil.<LauncherBuilder>configure(launcher -> {
                launcher.gradleDaemonMaxIdleTime(0);
            }));
            builder.configuration(Constants.JAVA_IMPLEMENTATION);
            //builder.tag("v1.0.1.11");
            //builder.tag("v1.0.0");
            //builder.commit("defb5d3b5aa136737edf30a7da08927f0ef62255");
            //builder.branch("GroovyScriptSupport");
            //builder.branch("develop");
        });
        dependencyWrapper.setTest(test -> {
        });
        dependencyWrapper.onlyRegisterDependencyAndRunTests();
        dependencyWrapper.getProjectManager().getPersistenceManager().loadPersistentData();
        dependencyWrapper.getProjectManager().getGitManager().initRepos();
    }
}
