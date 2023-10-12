package io.github.srdjanv.localgitdependency.ideintegration;

import static io.github.srdjanv.localgitdependency.Constants.TASKS_GROUP_INTERNAL;

import com.github.bsideup.jabel.Desugar;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.ideintegration.adapters.Adapter;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.Nullable;

public class IDEManager extends ManagerBase implements IIDEManager {

    public IDEManager(Managers managers) {
        super(managers);
    }

    private SourceSetContainer rootSourceSetContainer;
    private Function<SourceSet, String[]> taskSupplier;

    @Override
    protected void managerConstructor() {
        rootSourceSetContainer = getProject().getRootProject().getExtensions().getByType(SourceSetContainer.class);
        // this is used to disable tasks for class compilation and so on
        if (GradleVersion.version(getProject().getGradle().getGradleVersion()).compareTo(GradleVersion.version("6.0"))
                >= 0) {
            taskSupplier = (sourceSet) -> new String[] {
                sourceSet.getCompileJavaTaskName(),
                sourceSet.getProcessResourcesTaskName(),
                sourceSet.getClassesTaskName(),
                sourceSet.getJavadocJarTaskName(),
                sourceSet.getSourcesJarTaskName()
            };
        } else {
            taskSupplier = (sourceSet) -> new String[] {
                sourceSet.getCompileJavaTaskName(),
                sourceSet.getProcessResourcesTaskName(),
                sourceSet.getClassesTaskName()
            };
        }
    }

    @Override
    public boolean handelSourceSets() {
        boolean didWork = false;
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (dependency.isIdeSupportEnabled()) {
                didWork = true;
                handleIdeSupport(dependency);
            }
        }
        return didWork;
    }

    private void handleIdeSupport(Dependency dependency) {
        final var rootProject = getProject().getRootProject();
        // create source sets of the dependency
        for (SourceSetData sourceSetData :
                dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            var sourceSet =
                    rootSourceSetContainer.create(getSourceSetName(dependency, sourceSetData), sourceSetConf -> {
                        for (DirectorySetData directorySet : sourceSetData.getDirectorySetData()) {
                            switch (directorySet.getType()) {
                                case Java -> Adapter.JAVA.configure(sourceSetConf, directorySet, rootProject);
                            }
                        }
                        sourceSetConf.resources(conf -> {
                            conf.setSrcDirs(sourceSetData.getResources());
                            if (sourceSetData.getBuildResourcesDir() != null)
                                conf.getDestinationDirectory()
                                        .set(rootProject.file(sourceSetData.getBuildResourcesDir()));
                        });
                    });

            for (String task : taskSupplier.apply(sourceSet)) {
                try {
                    var sourceTask = rootProject.getTasks().getByName(task);
                    sourceTask.setEnabled(false);
                    sourceTask.setGroup(TASKS_GROUP_INTERNAL);
                } catch (UnknownTaskException ignore) {
                }
            }
        }
        for (SourceSetData sourceSetData :
                dependency.getPersistentInfo().getProbeData().getSourceSetsData()) {
            SourceSet depSourceSet = getSourceSetByName(rootSourceSetContainer, dependency, sourceSetData);

            // configure source set classpath
            if (!sourceSetData.getCompileClasspath().isEmpty()) {
                depSourceSet.setCompileClasspath(rootProject.getLayout().files(sourceSetData.getCompileClasspath()));
            }

            // link source sets to each other
            for (String dependentSourceSetName : sourceSetData.getDependentSourceSets()) {
                final Optional<SourceSetData> data =
                        dependency.getPersistentInfo().getProbeData().getSourceSetsData().stream()
                                .filter(probe -> probe.getName().equals(dependentSourceSetName))
                                .findFirst();

                if (data.isPresent()) {
                    final SourceSetData sourceData = data.get();
                    final var set = getSourceSetByName(rootSourceSetContainer, dependency, sourceData);

                    depSourceSet.setCompileClasspath(
                            depSourceSet.getCompileClasspath().plus(set.getOutput()));
                }
            }

            // link source sets to the main project using mappers
            for (SourceSetMapper.Mapping mapping :
                    new SourceSetMapper(dependency.getSourceSetMappings()).getMappings()) {
                final Set<String> dependentSourceSets = new HashSet<>();
                for (String dependentSourceSetName : mapping.dependents()) {
                    if (sourceSetData.getName().equals(dependentSourceSetName)) {
                        dependentSourceSets.add(dependentSourceSetName);
                        if (mapping.recursive()) {
                            dependentSourceSets.addAll(sourceSetData.getDependentSourceSets());
                        }
                    }
                }

                for (String dependentSourceSetName : dependentSourceSets) {
                    final SourceSet projectSet;
                    if (getProject() == rootProject) {
                        projectSet = mapping.targetSourceSet();
                    } else {
                        var subProjectSourceSetContainer =
                                getProject().getExtensions().getByType(SourceSetContainer.class);
                        projectSet = subProjectSourceSetContainer.getByName(
                                mapping.targetSourceSet().getName()); // TODO: 25/08/2023 probably not needed, test
                    }

                    final var sourceData = dependency.getPersistentInfo().getProbeData().getSourceSetsData().stream()
                            .filter(probe -> probe.getName().equals(dependentSourceSetName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(String.format(
                                    "Source set %s not found for dependency %s",
                                    dependentSourceSetName, dependency.getName())));

                    final var set = getSourceSetByName(rootSourceSetContainer, dependency, sourceData);
                    projectSet.setCompileClasspath(
                            projectSet.getCompileClasspath().plus(set.getOutput()));
                }
            }
        }
    }

    private SourceSet getSourceSetByName(
            SourceSetContainer sourceSetContainer, Dependency dependency, SourceSetData sourceSetData) {
        return sourceSetContainer.getByName(getSourceSetName(dependency, sourceSetData));
    }

    private String getSourceSetName(Dependency dependency, SourceSetData sourceSetData) {
        return Constants.PLUGIN_NAME + "." + getProject().getName() + "." + dependency.getName() + "."
                + sourceSetData.getName();
    }

    private class SourceSetMapper {
        private final List<Mapping> mappings;

        public SourceSetMapper(
                @Nullable List<io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper.Mapping> mappings) {
            if (mappings == null) {
                var lgdide = getConfigManager().getLGDExtensionByType(LGDIDE.class);
                this.mappings = Collections.singletonList(new Mapping(
                        "Default_Mapping",
                        lgdide.getDefaultTargetSourceSet().get(),
                        lgdide.getDefaultDepMappings().get(),
                        lgdide.getRecursive().get()));
            } else {
                this.mappings = mappings.stream()
                        .map(mapping -> new Mapping(
                                mapping.getName(),
                                mapping.getTargetSourceSet().get(),
                                mapping.getDependents().get(),
                                mapping.getRecursive().get()))
                        .collect(Collectors.toList());
            }
        }

        public List<Mapping> getMappings() {
            return mappings;
        }

        @Desugar
        private record Mapping(String name, SourceSet targetSourceSet, List<String> dependents, boolean recursive) {}
    }
}
