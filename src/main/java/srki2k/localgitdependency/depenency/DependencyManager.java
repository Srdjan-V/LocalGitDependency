package srki2k.localgitdependency.depenency;

import groovy.lang.Closure;
import org.gradle.api.Project;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.git.GitManager;
import srki2k.localgitdependency.property.Property;
import srki2k.localgitdependency.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();

    public void registerDependency(String configurationName, String dependencyURL, Closure<?> configureClosure) {
        Property.Builder dependencyProperty = new Property.Builder(dependencyURL);

        if (configureClosure != null) {
            configureClosure.setDelegate(dependencyProperty);
            configureClosure.call();
        }

        dependencies.add(new Dependency(configurationName, new Property(dependencyProperty)));
    }

    public void registerGitDependencies() {
        Instances.getGradleApiManager().createMainInitScript();
        boolean expressions = false;
        for (Dependency dependency : dependencies) {
            if (GitManager.initRepo(dependency)) {
                expressions = true;
                continue;
            }
            Instances.getGradleApiManager().createDependencyInitScript(dependency);
        }
        if (expressions) {
            List<List<Exception>> exceptionList = new ArrayList<>();
            for (Dependency dependency : dependencies) {
                exceptionList.add(dependency.getGitExceptions());
            }
            RuntimeException runtimeException = new RuntimeException("Exception(s) occurred while interacting with git");
            exceptionList.stream().flatMap(List::stream).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
    }

    // TODO: 06/02/2023  make a better implementation
    public void buildDependencies(boolean explicitBuild) {
        for (Dependency dependency : dependencies) {
            if (!dependency.getDir().exists()) {
                Logger.error("Dependency {} was not cloned, skipping build", dependency.getName());
                continue;
            }

            if (explicitBuild || !dependency.isManualBuild()) {
                try {
                    Instances.getGradleApiManager().buildGradleProject(dependency);
                } catch (Exception exception) {
                    Logger.error("Exception thrown while building Dependency {}", dependency.getName());
                    Logger.error("Exception {}", exception);
                }
                continue;
            }


/*            if (dependency.isManualBuild()) {
                if (GitUtils.hasLocalChangesInDir(dependency.getDir())) {
                    Logger.warn("Dependency {} has local changes but is not being automatically built", dependency.getName());
                }
            }*/
        }
    }

    public void addBuiltJarsAsDependencies(Dependency dependency) {
        Path libs = Constants.buildDir.apply(dependency.getDir()).toPath();

        if (!Files.exists(libs)) {
            Logger.error("Dependency {} was cloned, but no libs folder was found", dependency.getName());
            return;
        }

        Object[] deeps;
        try (Stream<Path> jars = Files.list(libs)) {
            List<Object> list = new ArrayList<>();
            jars.forEach(list::add);
            deeps = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                deeps[i] = list.get(i);
            }
        } catch (IOException exception) {
            Logger.error("Exception thrown while building Dependency {}", dependency.getName());
            Logger.error("Exception {}", exception);
            return;
        }

        if (deeps.length == 0) {
            Logger.error("Dependency {} was cloned, but no libs where found", dependency.getName());
            return;
        } else {
            Logger.warn("Adding the following Dependency {}, and its jars", dependency.getName());
            Logger.warn(Arrays.toString(deeps));
        }

        Project project = Instances.getProject();
        project.getDependencies().add(dependency.getConfigurationName(), project.getLayout().files(deeps));
    }

    public void addMavenJarsAsDependencies(Dependency dependency) {
        // TODO: 05/02/2023
    }

    public void addBuiltDependencies() {
        for (Dependency dependency : dependencies) {
            switch (dependency.getDependencyType()) {
                case Jar:
                    addBuiltJarsAsDependencies(dependency);
                    break;
                case MavenLocal:
                    addMavenJarsAsDependencies(dependency);
            }
        }
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }
}
