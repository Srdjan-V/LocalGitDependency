package srki2k.localgitdependency.depenency;

import groovy.lang.Closure;
import org.gradle.api.Project;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.util.GitUtils;
import srki2k.localgitdependency.util.GradleUtil;
import srki2k.localgitdependency.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DependencyManager {
    private final Set<Dependency> dependencies = new HashSet<>();
    private final Dependency.DefaultProperty defaultGlobalProperty;

    {
        defaultGlobalProperty = new Dependency.DefaultProperty();
        defaultGlobalProperty.defaultConfiguration(Constants.JAVA_IMPLEMENTATION);
        defaultGlobalProperty.initScript(Constants.initScriptDirs.apply(Constants.libDirs.get()));
        defaultGlobalProperty.dir(Constants.libDirs.get());
        defaultGlobalProperty.dependencyType(Dependency.DependencyType.MavenLocal);
        defaultGlobalProperty.keepGitUpdated(true);
        defaultGlobalProperty.manualBuild(false);
    }

    private Dependency.DefaultProperty globalProperty;

    public void globalProperty(Closure<?> configureClosure) {
        if (configureClosure != null) {
            Dependency.DefaultProperty defaultProperty = new Dependency.DefaultProperty();
            configureClosure.setDelegate(defaultProperty);
            configureClosure.call();
            this.globalProperty = defaultProperty;
        }
    }

    public void registerDependency(String configurationName, String dependencyURL, Closure<?> configureClosure) {
        Dependency.Property dependencyProperty = new Dependency.Property(dependencyURL);

        if (configureClosure != null) {
            configureClosure.setDelegate(dependencyProperty);
            configureClosure.call();
        }

        dependencies.add(new Dependency(configurationName, dependencyProperty, globalProperty, defaultGlobalProperty));
    }

    public void registerGitDependencies() {
        for (Dependency dependency : dependencies) {
            GitUtils.setupGit(dependency);
            GradleUtil.createInitScript(dependency);
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
                    GradleUtil.buildGradleProject(dependency);
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
                case Jar: addBuiltJarsAsDependencies(dependency);
                break;
                case MavenLocal: addMavenJarsAsDependencies(dependency);
            }
        }
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }
}
