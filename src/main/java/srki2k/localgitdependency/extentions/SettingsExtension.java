package srki2k.localgitdependency.extentions;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.property.DefaultProperty;
import srki2k.localgitdependency.property.Property;

@SuppressWarnings("unused")
public class SettingsExtension {

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
        Instances.getPropertyManager().globalProperty(configureClosure);
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
        Instances.getDependencyManager().registerDependency(configurationName, dependencyURL, configureClosure);
    }

}
