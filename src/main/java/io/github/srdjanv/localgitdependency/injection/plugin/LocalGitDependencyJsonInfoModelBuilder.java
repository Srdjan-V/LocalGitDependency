package io.github.srdjanv.localgitdependency.injection.plugin;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.injection.model.DefaultLocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeDataGetters;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.RepositoryDataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        boolean hasJavaPlugin = project.getExtensions().findByName("java") != null;
        boolean hasMavenPublishPlugin = project.getExtensions().findByName("maven-publish") != null;

        List<TaskData> appropriateTasks = queueAppropriateTasks(project, hasJavaPlugin);
        PublicationData publicationData = queueAppropriateMavenPublications(project, appropriateTasks, hasMavenPublishPlugin);
        int[] gradleVersion = Arrays.stream(project.getGradle().getGradleVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();
        JavaVersion javaVersion = null;
        boolean canProjectUseWithSourcesJar = true;
        boolean canProjectUseWithJavadocJar = true;
        if (hasJavaPlugin) {
            JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
            javaVersion = javaPluginExtension.getSourceCompatibility();

            if (gradleVersion[0] >= 7 && gradleVersion[1] >= 1) {
                SourceSet main = javaPluginExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                for (Task task : project.getTasks()) {
                    if (task.getName().equals(main.getSourcesJarTaskName())) {
                        canProjectUseWithSourcesJar = false;
                    }

                    if (task.getName().equals(main.getJavadocJarTaskName())) {
                        canProjectUseWithJavadocJar = false;
                    }
                }
            }
        }

        final JavaVersion finalJavaVersion = javaVersion;
        final boolean finalCanProjectUseWithSourcesJar = canProjectUseWithSourcesJar;
        final boolean finalCanProjectUseWithJavadocJar = canProjectUseWithJavadocJar;
        ProjectProbeDataGetters projectProbeData = ProjectProbeData.create(probe -> {
            probe.setVersion(Constants.PROJECT_VERSION);
            probe.setProjectId(project.getGroup() + ":" + project.getName() + ":" + project.getVersion());
            probe.setProjectGradleVersion(project.getGradle().getGradleVersion());
            probe.setJavaVersion(finalJavaVersion);
            probe.setCanProjectUseWithSourcesJar(finalCanProjectUseWithSourcesJar);
            probe.setCanProjectUseWithJavadocJar(finalCanProjectUseWithJavadocJar);
            probe.setHasJavaPlugin(hasJavaPlugin);
            probe.setHasMavenPublishPlugin(hasMavenPublishPlugin);
            probe.setTaskData(appropriateTasks);
            probe.setPublicationData(publicationData);
            probe.setSourceSetData(getSources(project));
            probe.setRepositoryList(RepositoryDataParser.create(project));
        });

        return new DefaultLocalGitDependencyJsonInfoModel(DataParser.projectProbeDataJson((ProjectProbeData) projectProbeData));
    }

    private static List<TaskData> queueAppropriateTasks(Project project, boolean hasJavaPlugin) {
        List<TaskData> defaultTaskObjectList = new ArrayList<>(2);
        String sourceTaskName = Constants.JarSourceTaskName.apply(project.getName());
        String javaDocTaskName = Constants.JarJavaDocTaskName.apply(project.getName());

        if (hasJavaPlugin) {
            for (Task task : project.getTasks()) {
                while (task.getName().equals(sourceTaskName)) {
                    sourceTaskName = sourceTaskName + System.currentTimeMillis();
                }

                while (task.getName().equals(javaDocTaskName)) {
                    javaDocTaskName = javaDocTaskName + System.currentTimeMillis();
                }
            }
        }

        final String finalSourceTaskName = sourceTaskName;
        defaultTaskObjectList.add(TaskData.create(data -> {
            data.setName(finalSourceTaskName);
            data.setClassifier("sources");
        }));
        final String finalJavaDocTaskName = javaDocTaskName;
        defaultTaskObjectList.add(TaskData.create(data -> {
            data.setName(finalJavaDocTaskName);
            data.setClassifier("javadoc");
        }));
        return defaultTaskObjectList;
    }


    private static PublicationData queueAppropriateMavenPublications(Project project, List<TaskData> appropriateTasks, boolean hasMavenPublishPlugin) {
        String publicationName = Constants.MavenPublicationName.apply(project.getName());
        String repositoryName = Constants.MavenRepositoryName.apply(project.getName());

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

        final String finalRepositoryName = repositoryName;
        final String finalPublicationName = publicationName;
        return PublicationData.create(data -> {
            data.setRepositoryName(finalRepositoryName);
            data.setPublicationName(finalPublicationName);
            data.setTasks(appropriateTasks);
        });
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
            SourceDirectorySet sourceDirectorySet = sourceSet.getJava();
            List<String> paths = new ArrayList<>();
            List<String> classpathDependencies = new ArrayList<>();
            List<String> fileClasspathDependencies = new ArrayList<>();

            for (File file : sourceDirectorySet.getSourceDirectories().getFiles()) {
                paths.add(file.getAbsolutePath());
            }

            for (Dependency dependency : project.getConfigurations().getByName(sourceSet.getCompileClasspathConfigurationName()).getAllDependencies()) {
                if (dependency instanceof FileCollectionDependency) {
                    FileCollection files = ((FileCollectionDependency) dependency).getFiles();
                    for (File file : files.getFiles()) {
                        fileClasspathDependencies.add(file.getAbsolutePath());
                    }
                    continue;
                }
                classpathDependencies.add(dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion());
            }

            sourceSets.add(SourceSetData.create(data -> {
                data.setName(sourceSet.getName());
                data.setClasspathConfigurationName(sourceSet.getCompileClasspathConfigurationName());
                data.setSources(paths);
                data.setRepositoryClasspathDependencies(classpathDependencies);
                data.setFileClasspathDependencies(fileClasspathDependencies);
            }));
        }

        return sourceSets;
    }

}
