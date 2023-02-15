package srki2k.localgitdependency.extentions;

import groovy.lang.Closure;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;

@SuppressWarnings("unused")
public class SettingsExtension {

    public Dependency.DependencyType getMavenFileLocal() {
        return Dependency.DependencyType.MavenFileLocal;
    }

    public Dependency.DependencyType getMavenLocal() {
       return Dependency.DependencyType.MavenLocal;
    }

    public Dependency.DependencyType getJar() {
        return Dependency.DependencyType.Jar;
    }

    public void configureGlobal(Closure<?> configureClosure) {
        Instances.getPropertyManager().globalProperty(configureClosure);
    }

    public void add(String dependencyURL) {
        add(null, dependencyURL, null);
    }

    public void add(String dependencyURL, Closure<?> configureClosure) {
        add(null, dependencyURL, configureClosure);
    }

    public void add(String configurationName, String dependencyURL) {
        add(configurationName, dependencyURL, null);
    }

    public void add(String configurationName, String dependencyURL, Closure<?> configureClosure) {
        Instances.getDependencyManager().registerDependency(configurationName, dependencyURL, configureClosure);
    }

}
