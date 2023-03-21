package io.github.srdjanv.localgitdependency.extentions;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.gradle.internal.metaobject.DynamicInvokeResult;
import org.gradle.internal.metaobject.MethodAccess;
import org.gradle.internal.metaobject.MethodMixIn;

@SuppressWarnings("unused")
public class LocalGitDependencyExtension extends ManagerBase implements MethodMixIn {
    private MethodAccess methodAccess;

    public LocalGitDependencyExtension(ProjectInstances projectInstances) {
        super(projectInstances);
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

    public void configureGlobal(
            @DelegatesTo(value = GlobalProperty.Builder.class, strategy = Closure.DELEGATE_FIRST)
            Closure<GlobalProperty.Builder> configureClosure) {
        getPropertyManager().globalProperty(configureClosure);
    }

    public void add(String dependencyURL) {
        add(null, dependencyURL, null);
    }

    public void add(String dependencyURL,
                    @DelegatesTo(value = DependencyProperty.Builder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure<DependencyProperty.Builder> configureClosure) {
        add(null, dependencyURL, configureClosure);
    }

    public void add(String configurationName, String dependencyURL) {
        add(configurationName, dependencyURL, null);
    }

    public void add(String configurationName, String dependencyURL,
                    @DelegatesTo(value = DependencyProperty.Builder.class, strategy = Closure.DELEGATE_FIRST)
                    Closure<DependencyProperty.Builder> configureClosure) {
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
