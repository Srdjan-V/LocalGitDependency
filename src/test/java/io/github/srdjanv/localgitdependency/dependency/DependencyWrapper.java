package io.github.srdjanv.localgitdependency.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import io.github.srdjanv.localgitdependency.property.DependencyBuilder;
import io.github.srdjanv.localgitdependency.property.GlobalBuilder;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.function.Consumer;

public class DependencyWrapper {
    private IProjectManager projectManager;
    private final String dependencyName;
    private final String gitUrl;
    private String testName;
    private State state;
    private Consumer<DependencyWrapper> test;
    private Closure<GlobalBuilder> globalClosure;
    private Closure<DependencyBuilder> dependencyClosure;
    private Dependency dependencyReference;

    public DependencyWrapper(DependencyRegistry registry) {
        state = State.Starting;
        this.dependencyName = registry.dependencyName;
        this.gitUrl = registry.gitUrl;
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

    public void setGlobalClosure(Consumer<GlobalBuilder> globalClosure) {
        this.globalClosure = new Closure<GlobalBuilder>(null) {
            public GlobalBuilder doCall() {
                GlobalBuilder builder = (GlobalBuilder) getDelegate();
                globalClosure.accept(builder);
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

    private void setGlobalConfiguration() {
        if (globalClosure != null)
            projectManager.getLocalGitDependencyExtension().configureGlobal(globalClosure);
    }

    private void registerDepToExtension() {
        projectManager.getLocalGitDependencyExtension().add(gitUrl, dependencyClosure);

        Optional<Dependency> optionalDependency = projectManager.getDependencyManager().getDependencies().stream().
                filter(dependency1 -> dependency1.getName().equals(getTestName())).findFirst();

        dependencyReference = optionalDependency.orElseThrow(() -> new RuntimeException("Dependency: " + getDependencyName() + " was not found in the DependencyManager"));
    }

    private void initPluginTasks() {
        projectManager.startPlugin();
    }

    public void onlyRegisterDependencyAndRunTests() {
        projectManager = ProjectInstance.getManager(ProjectInstance.createProject());
        setState(State.OnlyDependencyRegistered);
        checkDependencyState();

        setGlobalConfiguration();
        registerDepToExtension();

        test.accept(this);
    }

    public void startPluginAndRunTests() {
        projectManager = ProjectInstance.getManager(ProjectInstance.createProject());
        setState(State.Complete);
        checkDependencyState();

        setGlobalConfiguration();
        registerDepToExtension();
        initPluginTasks();

        test.accept(this);
    }

    private void checkDependencyState() {
        Assertions.assertNotNull(testName, "testName cant be null");
        if (dependencyClosure == null) {
            dependencyClosure = new Closure<DependencyBuilder>(null) {
                public DependencyBuilder doCall() {
                    DependencyBuilder builder = (DependencyBuilder) getDelegate();
                    builder.name(getTestName());
                    return builder;
                }
            };
        }
    }


    private void setState(State state) {
        if (this.state != State.Starting) {
            throw new IllegalStateException();
        }
        this.state = state;
    }

    public enum State {
        Complete,
        OnlyDependencyRegistered,
        Starting
    }


}
