package io.github.srdjanv.localgitdependency.util.dep;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.LocalGitDependencyPlugin;
import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.property.DefaultProperty;
import io.github.srdjanv.localgitdependency.property.Property;

import java.util.Optional;
import java.util.function.Consumer;

public class DependencyWrapper {
    private final String dependencyName;
    private final String gitUrl;
    private String testName;
    private State state;
    private Consumer<DependencyWrapper> test;
    private Closure<DefaultProperty.Builder> globalClosure;
    private Closure<Property.Builder> dependencyClosure;
    private Dependency dependencyReference;

    DependencyWrapper(DependencyRegistry registry) {
        state = State.Starting;
        this.dependencyName = registry.dependencyName;
        this.gitUrl = registry.gitUrl;
    }

    public State getState() {
        return state;
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

    public void setGlobalClosure(Consumer<DefaultProperty.Builder> globalClosure) {
        this.globalClosure = new Closure<DefaultProperty.Builder>(null) {
            public DefaultProperty.Builder doCall() {
                DefaultProperty.Builder builder = (DefaultProperty.Builder) getDelegate();
                globalClosure.accept(builder);
                return builder;
            }
        };
    }

    public void setDependencyClosure(Consumer<Property.Builder> dependencyClosure) {
        this.dependencyClosure = new Closure<Property.Builder>(null) {
            public Property.Builder doCall() {
                Property.Builder builder = (Property.Builder) getDelegate();
                dependencyClosure.accept(builder);
                builder.name(dependencyName);
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
            Instances.getSettingsExtension().configureGlobal(globalClosure);
    }

    private void registerDepToExtension() {
        Instances.getSettingsExtension().add(gitUrl, dependencyClosure);

        Optional<Dependency> optionalDependency = Instances.getDependencyManager().getDependencies().stream().
                filter(dependency1 -> dependency1.getName().equals(getDependencyName())).findFirst();

        dependencyReference = optionalDependency.orElseThrow(() -> new RuntimeException("Dependency: " + getDependencyName() + " was not found in the DependencyManager"));
    }

    private void initPluginTasks() {
        LocalGitDependencyPlugin.startPlugin();
    }

    public void startDSLPluginAndRunTests() {
        setState(State.OnlyDependencyRegistered);
        checkDependencyState();
        ProjectInstance.createProject();

        setGlobalConfiguration();
        registerDepToExtension();

        test.accept(this);
    }

    public void startPluginAndRunTests() {
        setState(State.Complete);
        checkDependencyState();
        ProjectInstance.createProject();

        setGlobalConfiguration();
        registerDepToExtension();
        initPluginTasks();

        test.accept(this);
    }

    private void checkDependencyState() {
        if (dependencyClosure == null) {
            dependencyClosure = new Closure<Property.Builder>(null) {
                public Property.Builder doCall() {
                    Property.Builder builder = (Property.Builder) getDelegate();
                    builder.name(dependencyName);
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
