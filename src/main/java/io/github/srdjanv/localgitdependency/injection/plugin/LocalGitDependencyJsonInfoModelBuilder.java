package io.github.srdjanv.localgitdependency.injection.plugin;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.ideintegration.adapters.Adapter;
import io.github.srdjanv.localgitdependency.injection.model.DefaultLocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.plugin.invokers.*;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.*;

public final class LocalGitDependencyJsonInfoModelBuilder implements ToolingModelBuilder {
    private static final String MODEL_NAME = LocalGitDependencyJsonInfoModel.class.getName();
    private Project project;
    private ProjectProbeData.Builder builder;

    @Override
    public boolean canBuild(String modelName) {
        return modelName.equals(MODEL_NAME);
    }

    @Override
    public @NotNull Object buildAll(@NotNull String modelName, Project project) {
        var javaPlugin = project.getExtensions().findByName("java");
        if (javaPlugin == null) throw new IllegalStateException("This project is not using java");

        String lgdPluginVersion;
        try {
            var manager = project.getExtensions().getByName(Constants.LOCAL_GIT_DEPENDENCY_EXTENSION);
            Class<Constants> constantsClass = (Class<Constants>) manager.getClass().getClassLoader().loadClass(Constants.class.getCanonicalName());
            var field$PLUGIN_VERSION = constantsClass.getField("PLUGIN_VERSION");
            lgdPluginVersion = (String) field$PLUGIN_VERSION.get(null);

        } catch (UnknownDomainObjectException | ClassNotFoundException |
                 NoSuchFieldException | IllegalAccessException e) {
            lgdPluginVersion = null;
        }

        this.project = project;
        builder = new ProjectProbeData.Builder();
        builder.setPluginVersion(lgdPluginVersion);

        buildBasicProjectData();
        buildSources();

        try {
            buildSubDependencies();
        } catch (Throwable e) {
            builder.setSubDependencyData(Collections.emptyList());
            if (!Objects.equals(lgdPluginVersion, Constants.PLUGIN_VERSION)) {
                PluginLogger.error("The plugin versions might be incompatible for subDependency configuration", e);
            } else PluginLogger.error("Unexpected error while building sub dependencies", e);
        }

        var projectProbeData = builder.create();
        var json = DataParser.projectProbeDataJson(projectProbeData);
        return new DefaultLocalGitDependencyJsonInfoModel(json);
    }

    private void buildBasicProjectData() {
        final JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        final String projectId = project.getGroup() + ":" + project.getName() + ":" + project.getVersion();

        Boolean canProjectUseWithSourcesJar = null;
        Boolean canProjectUseWithJavadocJar = null;
        String archivesBaseName;

        var hasMavenPublishPlugin = project.getExtensions().findByName("maven-publish") != null;

        var gradleVersion = GradleVersion.version(project.getGradle().getGradleVersion());
        if (gradleVersion.compareTo(GradleVersion.version("7.1")) >= 0) {
            SourceSet main = javaPluginExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            var tasks = project.getTasks();
            try {
                tasks.getByName(main.getSourcesJarTaskName());
                canProjectUseWithSourcesJar = true;
            } catch (UnknownTaskException ignore) {
                canProjectUseWithSourcesJar = false;
            }
            try {
                tasks.getByName(main.getJavadocJarTaskName());
                canProjectUseWithJavadocJar = true;
            } catch (UnknownTaskException ignore) {
                canProjectUseWithJavadocJar = false;
            }

            var base = project.getExtensions().getByType(BasePluginExtension.class);
            archivesBaseName = base.getArchivesName().getOrElse(project.getName());
        } else {
            try {
                @SuppressWarnings("deprecation") var base = project.getConvention().getPlugin(BasePluginConvention.class);
                archivesBaseName = base.getArchivesBaseName();
            } catch (IllegalStateException ignore) {
                archivesBaseName = project.getName();
            }
        }

        builder.setCanProjectUseWithSourcesJar(canProjectUseWithSourcesJar)
                .setCanProjectUseWithJavadocJar(canProjectUseWithJavadocJar)
                .setProjectId(projectId)
                .setArchivesBaseName(archivesBaseName)
                .setJavaVersion(javaPluginExtension.getTargetCompatibility())
                .setProjectGradleVersion(project.getGradle().getGradleVersion());
    }

    private void buildSources() {
        SourceSetContainer sourceContainer;
        try {
            sourceContainer = project.getExtensions().getByType(SourceSetContainer.class);
        } catch (UnknownDomainObjectException ignore) {
            builder.setSourceSetsData(Collections.emptyList());
            return;
        }


        List<SourceSetData> sourceSets = new ArrayList<>();
        for (SourceSet sourceSet : sourceContainer) {
            final String buildResourcesDir;
            if (sourceSet.getOutput().getResourcesDir() == null) {
                buildResourcesDir = "";
            } else {
                buildResourcesDir = sourceSet.getOutput().getResourcesDir().getAbsolutePath();
            }

            final List<String> compileClasspath = new ArrayList<>();
            final Set<String> dependentSourceSets = new HashSet<>();

            topFor:
            for (File file : sourceSet.getCompileClasspath()) {
                var absolutePath = file.getAbsolutePath();

                for (SourceSet innerSourceSet : sourceContainer) {
                    for (File classesDir : innerSourceSet.getOutput().getClassesDirs())
                        if (absolutePath.equals(classesDir.getAbsolutePath())) {
                            dependentSourceSets.add(innerSourceSet.getName());
                            continue topFor;
                        }

                    var resourcesDir = innerSourceSet.getOutput().getResourcesDir();
                    if (resourcesDir != null)
                        if (absolutePath.equals(resourcesDir.getAbsolutePath())) {
                            dependentSourceSets.add(innerSourceSet.getName());
                            continue topFor;
                        }
                }

                compileClasspath.add(absolutePath);
            }

            final List<String> resourcePaths = sourceSet.getResources().getSrcDirs().stream()
                    .filter(File::exists).map(File::getAbsolutePath).collect(ArrayList::new, List::add, List::addAll);

            var builder = SourceSetData.builder().
                    setName(sourceSet.getName()).
                    addDirectorySet(buildJavaDirectorySet(sourceSet)).
                    setBuildResourcesDir(buildResourcesDir).
                    setDependentSourceSets(dependentSourceSets).
                    setCompileClasspath(compileClasspath).
                    setResources(resourcePaths);

            sourceSets.add(builder.create());
        }

        builder.setSourceSetsData(sourceSets);
    }

    private DirectorySetData buildJavaDirectorySet(SourceSet sourceSet) {
        final List<String> sourcePaths = sourceSet.getJava().getSrcDirs().stream()
                .filter(File::exists).map(File::getAbsolutePath).collect(ArrayList::new, List::add, List::addAll);

        var gradleVersion = GradleVersion.version(project.getGradle().getGradleVersion());
        var builder = DirectorySetData.builder().setSources(sourcePaths).setType(Adapter.Types.Java);
        if (gradleVersion.compareTo(GradleVersion.version("6.1")) >= 0) {
            builder.setBuildClassesDir(sourceSet.getJava().getClassesDirectory().get().getAsFile().getAbsolutePath());
        } else {
            //noinspection deprecation
            builder.setBuildClassesDir(sourceSet.getJava().getOutputDir().getAbsolutePath());
        }

        return builder.create();
    }

    private void buildSubDependencies() throws Throwable {
        List<SubDependencyData> subDependencyDataList = new ArrayList<>();
        Collection<Object> dependencies;

        try {
            // The lgd object is loaded by a different class loader
            Object lgd = project.getExtensions().getByName(Constants.LOCAL_GIT_DEPENDENCY_MANAGER_INSTANCE_EXTENSION);
            var method$getDependencyManager = lgd.getClass().getDeclaredMethod("getDependencyManager");
            method$getDependencyManager.setAccessible(true);

            Object dependencyManager = method$getDependencyManager.invoke(lgd);
            var method$getDependencies = dependencyManager.getClass().getDeclaredMethod("getDependencies");
            method$getDependencies.setAccessible(true);

            dependencies = (Collection<Object>) method$getDependencies.invoke(dependencyManager);
        } catch (UnknownDomainObjectException e) {
            builder.setSubDependencyData(subDependencyDataList);
            return;
        }

        var lookup = MethodHandles.lookup();
        //Used to dynamically generate method handles based on the loaded class
        DependencyClassInvoker depInvoker = null;
        ProjectProbeDataClassInvoker probeInvoker = null;
        PersistentInfoClassInvoker persistentInfoInvoker = null;
        GitInfoCLassInvoker gitInfoInvoker = null;
        SubDependencyClassInvoker subInvoker = null;

        for (Object dependency : dependencies) {
            var builder = SubDependencyData.builder();

            //Refresh the invokers if a class changes, this should not be possible
            depInvoker = DependencyClassInvoker.createInvoker(lookup, depInvoker, dependency);
            persistentInfoInvoker = PersistentInfoClassInvoker.createInvoker(lookup, persistentInfoInvoker, depInvoker);
            probeInvoker = ProjectProbeDataClassInvoker.createInvoker(lookup, probeInvoker, persistentInfoInvoker);
            gitInfoInvoker = GitInfoCLassInvoker.createInvoker(lookup, gitInfoInvoker, depInvoker);

            var depName = depInvoker.getName();
            builder.setName(depName).
                    setProjectID(probeInvoker.getProjectID()).
                    setDependencyType(depInvoker.getDependencyType()).
                    setGitDir(gitInfoInvoker.getDir().getAbsolutePath()).
                    setArchivesBaseName(probeInvoker.getArchivesBaseName());

            var mavenFolder = depInvoker.getMavenFolder();
            if (mavenFolder != null) {
                builder.setMavenFolder(mavenFolder.getAbsolutePath());
            }

            subDependencyDataList.add(builder.create());

            for (Object subDependencyData : probeInvoker.getSubDependencyData()) {
                subInvoker = SubDependencyClassInvoker.createInvoker(lookup, subInvoker, subDependencyData);
                subDependencyDataList.add(
                        SubDependencyData.builder().
                                setName(depName + ":" + subInvoker.getName()).
                                setProjectID(subInvoker.getProjectID()).
                                setDependencyType(subInvoker.getDependencyType()).
                                setGitDir(subInvoker.getGitDir()).
                                setArchivesBaseName(subInvoker.getArchivesBaseName()).
                                setMavenFolder(subInvoker.getMavenFolder()).create());
            }
        }

        builder.setSubDependencyData(subDependencyDataList);
    }

}
