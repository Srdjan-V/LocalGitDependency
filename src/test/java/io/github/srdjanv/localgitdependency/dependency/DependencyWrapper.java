package io.github.srdjanv.localgitdependency.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.config.plugin.PluginBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.function.Consumer;

public class DependencyWrapper {
    private IProjectManager projectManager;
    private final String dependencyName;
    private final String gitUrl;
    private final String gitRev;
    private String testName;
    private State state;
    private Consumer<DependencyWrapper> test;
    private Closure<PluginBuilder> pluginClosure;
    private Closure<DefaultableBuilder> defaultableClosure;
    private Closure<DependencyBuilder> dependencyClosure;
    private Dependency dependencyReference;
    private String[] startupTasks;

    public DependencyWrapper(DependencyRegistry registry) {
        state = State.Starting;
        this.dependencyName = registry.dependencyName;
        this.gitUrl = registry.gitUrl;
        this.gitRev = registry.gitRev;
        this.startupTasks = registry.startupTasks;
    }

    public State getState() {
        return state;
    }

    public IProjectManager getProjectManager() {
        return projectManager;
    }

    public String getDependencyName() {
        return dependencyName;
    }

    public Dependency getDependency() {
        switch (state) {
            case OnlyDependencyRegistered:
            case Complete:
                return dependencyReference;

            default:
                throw new IllegalStateException();
        }
    }

    public void setPluginClosure(Consumer<PluginBuilder> pluginClosure) {
        this.pluginClosure = new Closure<PluginBuilder>(null) {
            public PluginBuilder doCall() {
                PluginBuilder builder = (PluginBuilder) getDelegate();
                pluginClosure.accept(builder);
                return builder;
            }
        };
    }

    public void setDependencyClosure(Consumer<DependencyBuilder> dependencyClosure) {
        this.dependencyClosure = new Closure<DependencyBuilder>(null) {
            public DependencyBuilder doCall() {
                DependencyBuilder builder = (DependencyBuilder) getDelegate();
                dependencyClosure.accept(builder);
                builder.name(getTestName());
                builder.commit(gitRev);
                builder.buildLauncher(ClosureUtil.configure((LauncherBuilder launcher) -> {
                    launcher.startup(ClosureUtil.configure((Launchers.Startup startup) -> {
                        startup.mainTasks(startupTasks);
                    }));
                }));
                return builder;
            }
        };
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = dependencyName + testName;
    }

    public void setTest(Consumer<DependencyWrapper> test) {
        this.test = test;
    }

    private void setPluginConfiguration() {
        if (pluginClosure != null)
            projectManager.getLocalGitDependencyExtension().configurePlugin(pluginClosure);
    }

    private void setDefaultableConfiguration() {
        if (defaultableClosure != null)
            projectManager.getLocalGitDependencyExtension().configureDefaultable(defaultableClosure);
    }

    private void registerDepToExtension() {
        projectManager.getLocalGitDependencyExtension().add(gitUrl, dependencyClosure);
    }

    private void resolveDep() {
        Optional<Dependency> optionalDependency = projectManager.getDependencyManager().getDependencies().stream().
                filter(dependency1 -> dependency1.getName().equals(getTestName())).findFirst();

        dependencyReference = optionalDependency.orElseThrow(() -> new RuntimeException("Dependency: " + getDependencyName() + " was not found in the DependencyManager"));
    }

    private void initPluginTasks() {
        projectManager.startPlugin();
    }

    public void onlyRegisterDependencyAndRunTests() {
        projectManager = ProjectInstance.getProjectManager(ProjectInstance.createProject());
        checkDependencyState();

        setPluginConfiguration();
        setDefaultableConfiguration();
        registerDepToExtension();
        projectManager.getConfigManager().configureConfigs();
        setState(State.OnlyDependencyRegistered);

        test.accept(this);
    }

    public void startPluginAndRunTests() {
        projectManager = ProjectInstance.getProjectManager(ProjectInstance.createProject());
        checkDependencyState();

        setPluginConfiguration();
        setDefaultableConfiguration();
        registerDepToExtension();
        initPluginTasks();
        setState(State.Complete);

        test.accept(this);
    }

    private void checkDependencyState() {
        Assertions.assertNotNull(testName, "testName cant be null");
        if (dependencyClosure == null) {
            dependencyClosure = new Closure<DependencyBuilder>(null) {
                public DependencyBuilder doCall() {
                    DependencyBuilder builder = (DependencyBuilder) getDelegate();
                    builder.name(getTestName());
                    builder.buildLauncher(ClosureUtil.configure((LauncherBuilder launcher) -> {
                        launcher.startup(ClosureUtil.configure((Launchers.Startup startup) -> {
                            startup.mainTasks(startupTasks);
                        }));
                    }));
                    return builder;
                }
            };
        }
    }

    private void setState(State state) {
        if (this.state != State.Starting) {
            throw new IllegalStateException();
        }
        resolveDep();
        this.state = state;
    }

    public enum State {
        Complete,
        OnlyDependencyRegistered,
        Starting
    }


}
