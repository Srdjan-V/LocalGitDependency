package io.github.srdjanv.localgitdependency.injection.plugin;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.injection.model.DefaultLocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryDataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
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

// TODO: 09/03/2023 Rewrite the logic
public class LocalGitDependencyJsonInfoModelBuilder implements ToolingModelBuilder {
    private static final String MODEL_NAME = LocalGitDependencyJsonInfoModel.class.getName();

    @Override
    public boolean canBuild(String modelName) {
        return modelName.equals(MODEL_NAME);
    }

    @Override
    public @NotNull Object buildAll(@NotNull String modelName, Project project) {
        var javaPlugin = project.getExtensions().findByName("java");
        if (javaPlugin == null) {
            throw new RuntimeException();
        }

        boolean hasMavenPublishPlugin = project.getExtensions().findByName("maven-publish") != null;
        JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);

        List<TaskData> appropriateTasks = queueAppropriateTasks(project);
        PublicationData publicationData = queueAppropriateMavenPublications(project, appropriateTasks, hasMavenPublishPlugin);

        var gradleVersion = GradleVersion.version(project.getGradle().getGradleVersion());
        boolean canProjectUseWithSourcesJar = true;
        boolean canProjectUseWithJavadocJar = true;
        String archivesBaseName;
        String projectId = project.getGroup() + ":" + project.getName() + ":" + project.getVersion();
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
            archivesBaseName = base.getArchivesName().getOrElse(projectId);
        } else {
            // TODO: 05/05/2023
            // @SuppressWarnings("deprecation") var base = project.getExtensions().getByType(BasePluginConvention.class);
            // archivesBaseName = base.getArchivesBaseName();
            archivesBaseName = projectId;
        }

        ProjectProbeData.Builder builder = new ProjectProbeData.Builder();
        builder.setVersion(Constants.PROJECT_VERSION);
        builder.setProjectId(projectId);
        builder.setArchivesBaseName(archivesBaseName);
        builder.setProjectGradleVersion(project.getGradle().getGradleVersion());
        builder.setJavaVersion(javaPluginExtension.getTargetCompatibility());
        builder.setCanProjectUseWithSourcesJar(canProjectUseWithSourcesJar);
        builder.setCanProjectUseWithJavadocJar(canProjectUseWithJavadocJar);
        builder.setHasJavaPlugin(true);
        builder.setHasMavenPublishPlugin(hasMavenPublishPlugin);
        builder.setTaskData(appropriateTasks);
        builder.setPublicationData(publicationData);
        builder.setSourceSetsData(getSources(project));
        builder.setRepositoryList(RepositoryDataParser.create(project));

        return new DefaultLocalGitDependencyJsonInfoModel(DataParser.projectProbeDataJson(builder.create()));
    }

    private static List<TaskData> queueAppropriateTasks(Project project) {
        List<TaskData> defaultTaskObjectList = new ArrayList<>(2);
        String sourceTaskName = Constants.JarSourceTaskName.apply(project);
        String javaDocTaskName = Constants.JarJavaDocTaskName.apply(project);

        defaultTaskObjectList.add(new TaskData.Builder().
                setName(sourceTaskName).
                setClassifier("sources").
                create());

        defaultTaskObjectList.add(new TaskData.Builder().
                setName(javaDocTaskName).
                setClassifier("javadoc").
                create());
        return defaultTaskObjectList;
    }


    private static PublicationData queueAppropriateMavenPublications(Project project, List<TaskData> appropriateTasks, boolean hasMavenPublishPlugin) {
        String publicationName = Constants.MavenPublicationName.apply(project);
        String repositoryName = Constants.MavenRepositoryName.apply(project);

        if (hasMavenPublishPlugin) {
            DefaultPublishingExtension publishingExtension = project.getExtensions().getByType(DefaultPublishingExtension.class);

            List<DefaultMavenPublication> mavenPublications = publishingExtension.getPublications().stream()
                    .filter(publication -> publication instanceof DefaultMavenPublication)
                    .map(publication -> (DefaultMavenPublication) publication)
                    .collect(Collectors.toList());

            for (DefaultMavenPublication publication : mavenPublications) {
                while (publication.getName().equals(publicationName)) {
                    publicationName = publicationName + System.currentTimeMillis();
                }
            }

            List<DefaultMavenArtifactRepository> mavenArtifactRepositories = publishingExtension.getRepositories().stream()
                    .filter(repository -> repository instanceof DefaultMavenArtifactRepository)
                    .map(repository -> (DefaultMavenArtifactRepository) repository)
                    .collect(Collectors.toList());

            for (DefaultMavenArtifactRepository repository : mavenArtifactRepositories) {
                while (repository.getName().equals(repositoryName)) {
                    repositoryName = repositoryName + System.currentTimeMillis();
                }
            }

        }

        return new PublicationData.Builder().
                setRepositoryName(repositoryName).
                setPublicationName(publicationName).
                setTasks(appropriateTasks).
                create();
    }

    private static List<SourceSetData> getSources(Project project) {
        List<SourceSetData> sourceSets = new ArrayList<>();
        SourceSetContainer sourceContainer;
        try {
            sourceContainer = project.getExtensions().getByType(SourceSetContainer.class);
        } catch (UnknownDomainObjectException ignore) {
            return sourceSets;
        }

        for (SourceSet sourceSet : sourceContainer) {
            List<String> sourcePaths = new ArrayList<>();
            List<String> resourcePaths = new ArrayList<>();
            List<String> compileClasspath = new ArrayList<>();
            Set<String> dependentSourceSets = new HashSet<>();

            for (File file : sourceSet.getJava().getSourceDirectories().getFiles()) {
                sourcePaths.add(file.getAbsolutePath());
            }

            for (File file : sourceSet.getResources().getSourceDirectories().getFiles()) {
                resourcePaths.add(file.getAbsolutePath());
            }

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
                    setDependentSourceSets(dependentSourceSets).
                    setCompileClasspath(compileClasspath).
                    setSources(sourcePaths).
                    setResources(resourcePaths).
                    create());
        }

        return sourceSets;
    }

}
