package io.github.srdjanv.localgitdependency.dependency;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableConfig;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.extentions.LGD;
import io.github.srdjanv.localgitdependency.extentions.LGDHelper;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import io.github.srdjanv.localgitdependency.project.ProjectInstance;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.DependencyHandler;

public class DependencyWrapper {
    private final IProjectManager projectManager;
    private final String identifier;
    private final Action<DependencyConfig> configAction;
    private String testName;

    public DependencyWrapper(DependencyRegistry.Entry entry) {
        projectManager = ProjectInstance.getProjectManager(ProjectInstance.createProject());
        this.identifier = entry.name();
        this.configAction = entry.configAction();
    }

    public IProjectManager getProjectManager() {
        return projectManager;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTestName() {
        Objects.requireNonNull(testName);
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = (identifier + "$" + testName).trim().replace(".", "");
    }

    public void applyPluginConfiguration(Action<PluginConfig> action) {
        projectManager.getLGDExtensionByType(LGD.class).plugin(action);
    }

    public void applyDefaultableConfiguration(Action<DefaultableConfig> action) {
        projectManager.getLGDExtensionByType(LGD.class).defaults(action);
    }

    public void registerDepToExtension(Action<DependencyConfig> action) {
        var dep = projectManager
                .getDependencyManager()
                .registerDependency("https://github.com/Srdjan-V/LocalGitDependencyTestRepo.git");
        configAction.execute(dep);
        action.execute(dep);
    }

    public void registerDepToDependencies(List<Function<LGDHelper, ?>> actions) {
        var deps = projectManager.getProject().getDependencies();
        var lgdHelper = projectManager.getLGDExtensionByType(LGDHelper.class);
        for (Function<LGDHelper, ?> action : actions) {
            registerDepToDependencies(action, deps, lgdHelper);
        }
    }

    public void registerDepToDependencies(Function<LGDHelper, ?> action) {
        var deps = projectManager.getProject().getDependencies();
        var lgdHelper = projectManager.getLGDExtensionByType(LGDHelper.class);
        registerDepToDependencies(action, deps, lgdHelper);
    }

    private void registerDepToDependencies(Function<LGDHelper, ?> action, DependencyHandler deps, LGDHelper lgdHelper) {
        deps.add(Constants.JAVA_IMPLEMENTATION, action.apply(lgdHelper));
    }

    public Dependency getDependency() {
        Optional<Dependency> optionalDependency = projectManager.getDependencyManager().getDependencies().stream()
                .filter(dependency1 -> dependency1.getName().equals(getTestName()))
                .findFirst();

        return optionalDependency.orElseThrow(
                () -> new RuntimeException("Dependency: " + getTestName() + " was not found in the DependencyManager"));
    }
}
