package srki2k.localgitdependency.injection.plugin;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.injection.model.imp.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.imp.DefaultPublishingObject;
import srki2k.localgitdependency.injection.model.imp.DefaultTaskObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalGitDependencyInfoModelBuilder implements ToolingModelBuilder {
    private static final String MODEL_NAME = LocalGitDependencyInfoModel.class.getName();

    @Override
    public boolean canBuild(String modelName) {
        return modelName.equals(MODEL_NAME);
    }

    @Override
    public Object buildAll(String modelName, Project project) {
        boolean hasJavaPlugin = project.getExtensions().findByName("java") != null;
        boolean hasMavenPublishPlugin = project.getExtensions().findByName("maven-publish") != null;

        List<DefaultTaskObject> appropriateTasks = queueAppropriateTasks(project, hasJavaPlugin);
        DefaultPublishingObject defaultPublicationObject = queueAppropriateMavenPublications(project, appropriateTasks, hasMavenPublishPlugin);

        return new DefaultLocalGitDependencyInfoModel(
                project.getGroup() + ":" + project.getName() + ":" + project.getVersion(),
                project.getGradle().getGradleVersion(),
                hasJavaPlugin,
                hasMavenPublishPlugin,
                appropriateTasks,
                defaultPublicationObject);
    }

    private static List<DefaultTaskObject> queueAppropriateTasks(Project project, boolean hasJavaPlugin) {
        List<DefaultTaskObject> defaultTaskObjectList = new ArrayList<>(2);
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

        defaultTaskObjectList.add(new DefaultTaskObject(sourceTaskName, "sources"));
        defaultTaskObjectList.add(new DefaultTaskObject(javaDocTaskName, "javadoc"));
        return defaultTaskObjectList;
    }


    private static DefaultPublishingObject queueAppropriateMavenPublications(Project project, List<DefaultTaskObject> appropriateTasks, boolean hasMavenPublishPlugin) {
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

        return new DefaultPublishingObject(repositoryName, publicationName, appropriateTasks);
    }

}
