package io.github.srdjanv.localgitdependency.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.LocalGitDependencyPlugin;
import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ProjectManager;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.function.Consumer;

public class DependencyWrapper {
    private ProjectManager projectManager;
    private final String dependencyName;
    private final String gitUrl;
    private String testName;
    private State state;
    private Consumer<DependencyWrapper> test;
    private Closure<GlobalProperty.Builder> globalClosure;
    private Closure<DependencyProperty.Builder> dependencyClosure;
    private Dependency dependencyReference;

    public DependencyWrapper(DependencyRegistry registry) {
        state = State.Starting;
        this.dependencyName = registry.dependencyName;
        this.gitUrl = registry.gitUrl;
    }

    public State getState() {
        return state;
    }

    public ProjectManager getProjectManager() {
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

    public void setGlobalClosure(Consumer<GlobalProperty.Builder> globalClosure) {
        this.globalClosure = new Closure<GlobalProperty.Builder>(null) {
            public GlobalProperty.Builder doCall() {
                GlobalProperty.Builder builder = (GlobalProperty.Builder) getDelegate();
                globalClosure.accept(builder);
                return builder;
            }
        };
    }

    public void setDependencyClosure(Consumer<DependencyProperty.Builder> dependencyClosure) {
        this.dependencyClosure = new Closure<DependencyProperty.Builder>(null) {
            public DependencyProperty.Builder doCall() {
                DependencyProperty.Builder builder = (DependencyProperty.Builder) getDelegate();
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
        projectManager = LocalGitDependencyPlugin.getProject(ProjectInstance.createProject());
        setState(State.OnlyDependencyRegistered);
        checkDependencyState();

        setGlobalConfiguration();
        registerDepToExtension();

        test.accept(this);
    }

    public void startPluginAndRunTests() {
        projectManager = LocalGitDependencyPlugin.getProject(ProjectInstance.createProject());
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
            dependencyClosure = new Closure<DependencyProperty.Builder>(null) {
                public DependencyProperty.Builder doCall() {
                    DependencyProperty.Builder builder = (DependencyProperty.Builder) getDelegate();
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
