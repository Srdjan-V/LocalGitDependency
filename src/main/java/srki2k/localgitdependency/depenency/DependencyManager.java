package srki2k.localgitdependency.depenency;

import groovy.lang.Closure;
import org.gradle.api.Project;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.Logger;
import srki2k.localgitdependency.property.Property;

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

/*
    // TODO: 06/02/2023  make a better implementation
    public void buildDependencies(boolean explicitBuild) {
        for (Dependency dependency : dependencies) {
            if (!dependency.getGitInfo().getDir().exists()) {
                Logger.error("Dependency {} was not cloned, skipping build", dependency.getName());
                continue;
            }

            if (explicitBuild || !dependency.getGradleInfo().isManualBuild()) {
                try {
                    Instances.getGradleManager().buildGradleProject(dependency);
                } catch (Exception exception) {
                    Logger.error("Exception thrown while building Dependency {}", dependency.getName());
                    Logger.error("Exception {}", exception);
                }
                continue;
            }


*/
/*            if (dependency.isManualBuild()) {
                if (GitUtils.hasLocalChangesInDir(dependency.getDir())) {
                    Logger.warn("Dependency {} has local changes but is not being automatically built", dependency.getName());
                }
            }*//*

        }
    }
*/

    private void addBuiltJarsAsDependencies(Dependency dependency) {
        Path libs = Constants.buildDir.apply(dependency.getGitInfo().getDir()).toPath();

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

    private void addMavenJarsAsDependencies(Dependency dependency) {
        // TODO: 05/02/2023 add logging

        Project project = Instances.getProject();
        project.getDependencies().add(dependency.getConfigurationName(), dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel().getProjectId());
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
