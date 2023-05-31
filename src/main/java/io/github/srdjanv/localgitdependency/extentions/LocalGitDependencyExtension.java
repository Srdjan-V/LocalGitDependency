package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableBuilder;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyBuilder;
import io.github.srdjanv.localgitdependency.config.plugin.PluginBuilder;
import org.gradle.internal.metaobject.DynamicInvokeResult;
import org.gradle.internal.metaobject.MethodAccess;
import org.gradle.internal.metaobject.MethodMixIn;

@SuppressWarnings({"unused", "rawtypes"})
public class LocalGitDependencyExtension extends ManagerBase implements MethodMixIn {
    private MethodAccess methodAccess;

    public LocalGitDependencyExtension(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        methodAccess = new DynamicAddDependencyMethods();
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

    public void configurePlugin(
            @DelegatesTo(value = PluginBuilder.class, strategy = Closure.DELEGATE_FIRST)
            Closure configureClosure) {
        getPropertyManager().configurePlugin(configureClosure);
    }
    public void configureDefaultable(
            @DelegatesTo(value = DefaultableBuilder.class, strategy = Closure.DELEGATE_FIRST)
            Closure configureClosure) {
        getPropertyManager().configureDefaultable(configureClosure);
    }

    public void add(String dependencyURL) {
        add(null, dependencyURL, null);
    }

    public void add(String dependencyURL,
                    @DelegatesTo(value = DependencyBuilder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure configureClosure) {
        add(null, dependencyURL, configureClosure);
    }

    public void add(String configurationName, String dependencyURL) {
        add(configurationName, dependencyURL, null);
    }

    public void add(String configurationName, String dependencyURL,
                    @DelegatesTo(value = DependencyBuilder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure configureClosure) {
        getDependencyManager().registerDependency(configurationName, dependencyURL, configureClosure);
    }

    @Override
    public MethodAccess getAdditionalMethods() {
        return methodAccess;
    }

    private class DynamicAddDependencyMethods implements MethodAccess {

        @Override
        public boolean hasMethod(String name, Object... arguments) {
            if (getProject().getConfigurations().findByName(name) != null) {
                return true;
            }
            return name.endsWith(".git");
        }

        @Override
        public DynamicInvokeResult tryInvokeMethod(String name, Object... arguments) {
            if (getProject().getConfigurations().findByName(name) != null) {
                return tryAddingConfiguration(name, arguments);
            }
            if (name.endsWith(".git")) {
                return tryAddingGitUrl(name, arguments);
            }
            return DynamicInvokeResult.notFound();
        }

        private DynamicInvokeResult tryAddingConfiguration(String name, Object... arguments) {
            if (arguments.length == 1) {
                if (arguments[0] instanceof String) {
                    add(name, (String) arguments[0]);
                    return DynamicInvokeResult.found();
                }
            }

            if (arguments.length == 2) {
                if (arguments[0] instanceof String && arguments[1] instanceof Closure) {
                    add(name, (String) arguments[0], (Closure) arguments[1]);
                    return DynamicInvokeResult.found();
                }
            }

            return DynamicInvokeResult.notFound();
        }

        private DynamicInvokeResult tryAddingGitUrl(String name, Object... arguments) {
            if (arguments.length == 0) {
                add(name);
                return DynamicInvokeResult.found();
            }

            if (arguments.length == 1) {
                if (arguments[0] instanceof Closure) {
                    add(name, (Closure) arguments[0]);
                    return DynamicInvokeResult.found();
                }
            }

            return DynamicInvokeResult.notFound();
        }

    }

}
