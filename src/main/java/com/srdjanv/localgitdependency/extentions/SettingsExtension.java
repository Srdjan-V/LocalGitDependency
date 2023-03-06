package com.srdjanv.localgitdependency.extentions;

import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.project.ManagerBase;
import com.srdjanv.localgitdependency.project.ProjectInstances;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import com.srdjanv.localgitdependency.property.DefaultProperty;
import com.srdjanv.localgitdependency.property.Property;

@SuppressWarnings("unused")
public class SettingsExtension extends ManagerBase {
    public SettingsExtension(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    public Dependency.Type MavenLocal() {
        return Dependency.Type.MavenLocal;
    }

    public Dependency.Type MavenProjectLocal() {
        return Dependency.Type.MavenProjectLocal;
    }

    public Dependency.Type MavenProjectDependencyLocal() {
        return Dependency.Type.MavenProjectDependencyLocal;
    }

    public Dependency.Type JarFlatDir() {
       return Dependency.Type.JarFlatDir;
    }

    public Dependency.Type Jar() {
        return Dependency.Type.Jar;
    }

    public void configureGlobal(
            @DelegatesTo(value = DefaultProperty.Builder.class, strategy = Closure.DELEGATE_FIRST)
            Closure<DefaultProperty.Builder> configureClosure) {
        getPropertyManager().globalProperty(configureClosure);
    }

    public void add(String dependencyURL) {
        add(null, dependencyURL, null);
    }

    public void add(String dependencyURL,
                    @DelegatesTo(value = Property.Builder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure<Property.Builder> configureClosure) {
        add(null, dependencyURL, configureClosure);
    }

    public void add(String configurationName, String dependencyURL) {
        add(configurationName, dependencyURL, null);
    }

    public void add(String configurationName, String dependencyURL,
                    @DelegatesTo(value = Property.Builder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure<Property.Builder> configureClosure) {
        getDependencyManager().registerDependency(configurationName, dependencyURL, configureClosure);
    }

}
