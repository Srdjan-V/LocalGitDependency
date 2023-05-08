package io.github.srdjanv.localgitdependency.injection.plugin;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.injection.model.DefaultLocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalGitDependencyJsonInfoModelBuilder implements ToolingModelBuilder {
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
        if (javaPlugin == null) {
            throw new IllegalStateException("This project is not using java");
        }

        this.project = project;
        builder = new ProjectProbeData.Builder();
        builder.setVersion(Constants.PROJECT_VERSION);

        buildBasicProjectData();
        List<String> artifactTasksNames = buildArtifactTasks();
        buildMavenPublicationData(artifactTasksNames);
        buildSources();

        var projectProbeData = builder.create();
        var json = DataParser.projectProbeDataJson(projectProbeData);
        return new DefaultLocalGitDependencyJsonInfoModel(json);
    }

    private void buildBasicProjectData() {
        final JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        final String projectId = project.getGroup() + ":" + project.getName() + ":" + project.getVersion();

        boolean canProjectUseWithSourcesJar = true;
        boolean canProjectUseWithJavadocJar = true;
        String archivesBaseName;

        var gradleVersion = GradleVersion.version(project.getGradle().getGradleVersion());
        if (gradleVersion.compareTo(GradleVersion.version("7.1")) >= 0) {
            SourceSet main = javaPluginExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            for (Task task : project.getTasks()) {
                if (task.getName().equals(main.getSourcesJarTaskName())) {
                    canProjectUseWithSourcesJar = false;
                }

                if (task.getName().equals(main.getJavadocJarTaskName())) {
                    canProjectUseWithJavadocJar = false;
                }
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

    private List<String> buildArtifactTasks() {
        List<TaskData> artifactTasks = new ArrayList<>(2);

        {
            var sourceTaskName = "InitScriptSourceTaskForProject" + project.getName();
            for (Task task : project.getTasks()) {
                while (task.getName().equals(sourceTaskName)) {
                    sourceTaskName = sourceTaskName + Math.random();
                }
            }

            artifactTasks.add(TaskData.builder().
                    setName(sourceTaskName).
                    setClassifier("sources").
                    create());
        }

        {
            var javaDocTaskName = "InitScriptJavaDocTaskForProject" + project.getName();
            for (Task task : project.getTasks()) {
                while (task.getName().equals(javaDocTaskName)) {
                    javaDocTaskName = javaDocTaskName + Math.random();
                }
            }


            artifactTasks.add(TaskData.builder().
                    setName(javaDocTaskName).
                    setClassifier("javadoc").
                    create());
        }
        builder.setArtifactTasks(artifactTasks);
        return artifactTasks.stream().map(TaskData::getName).collect(Collectors.toList());
    }

    private void buildMavenPublicationData(List<String> artifactTasksNames) {
        var publicationName = "InitScriptPublicationForProject" + project.getName();
        var repositoryName = "InitScriptRepositoryForProject" + project.getName();

        var hasMavenPublishPlugin = project.getExtensions().findByName("maven-publish") != null;
        if (hasMavenPublishPlugin) {
            DefaultPublishingExtension publishingExtension = project.getExtensions().getByType(DefaultPublishingExtension.class);

            List<MavenPublication> mavenPublications = publishingExtension.getPublications().stream()
                    .filter(publication -> publication instanceof MavenPublication)
                    .map(publication -> (MavenPublication) publication)
                    .collect(Collectors.toList());

            for (MavenPublication publication : mavenPublications) {
                while (publication.getName().equals(publicationName)) {
                    publicationName = publicationName + Math.random();
                }
            }

            List<MavenArtifactRepository> mavenArtifactRepositories = publishingExtension.getRepositories().stream()
                    .filter(repository -> repository instanceof MavenArtifactRepository)
                    .map(repository -> (MavenArtifactRepository) repository)
                    .collect(Collectors.toList());

            for (MavenArtifactRepository repository : mavenArtifactRepositories) {
                while (repository.getName().equals(repositoryName)) {
                    repositoryName = repositoryName + Math.random();
                }
            }
        }

        var publicationData = PublicationData.builder().
                setRepositoryName(repositoryName).
                setPublicationName(publicationName).
                setTasks(artifactTasksNames).
                create();

        builder.setPublicationData(publicationData);
    }

    private void buildSources() {
        List<SourceSetData> sourceSets = new ArrayList<>();
        SourceSetContainer sourceContainer;
        try {
            sourceContainer = project.getExtensions().getByType(SourceSetContainer.class);
        } catch (UnknownDomainObjectException ignore) {
            builder.setSourceSetsData(sourceSets);
            return;
        }

        for (SourceSet sourceSet : sourceContainer) {
            final String buildClassesDir = sourceSet.getOutput().getClassesDirs().getAsPath();
            final String buildResourcesDir;
            if (sourceSet.getOutput().getResourcesDir() == null) {
                buildResourcesDir = "";
            } else {
                buildResourcesDir = sourceSet.getOutput().getResourcesDir().getAbsolutePath();
            }

            final List<String> sourcePaths = new ArrayList<>();
            final List<String> resourcePaths = new ArrayList<>();
            final List<String> compileClasspath = new ArrayList<>();
            final Set<String> dependentSourceSets = new HashSet<>();

            sourceSet.getJava().getSourceDirectories().getFiles().
                    stream().map(File::getAbsolutePath).collect(() -> sourcePaths, List::add, List::addAll);

            sourceSet.getJava().getSourceDirectories().getFiles().
                    stream().map(File::getAbsolutePath).collect(() -> resourcePaths, List::add, List::addAll);

            topFor:
            for (File file : sourceSet.getCompileClasspath()) {
                var absolutePath = file.getAbsolutePath();

                for (SourceSet source : sourceContainer) {
                    if (absolutePath.contains(source.getName()) && absolutePath.contains("build") && (absolutePath.contains("classes") || absolutePath.contains("resources"))) {
                        dependentSourceSets.add(source.getName());
                        continue topFor;
                    }
                }

                compileClasspath.add(absolutePath);
            }

            sourceSets.add(SourceSetData.builder().
                    setName(sourceSet.getName()).
                    setBuildClassesDir(buildClassesDir).
                    setBuildResourcesDir(buildResourcesDir).
                    setDependentSourceSets(dependentSourceSets).
                    setCompileClasspath(compileClasspath).
                    setSources(sourcePaths).
                    setResources(resourcePaths).
                    create());
        }

        builder.setSourceSetsData(sourceSets);
    }

}
