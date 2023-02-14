package srki2k.localgitdependency.injection.plugin;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.injection.model.imp.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.imp.DefaultPublicationObject;
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
        DefaultPublicationObject defaultPublicationObject = queueAppropriateMavenPublications(project, appropriateTasks, hasMavenPublishPlugin);

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

        defaultTaskObjectList.add(new DefaultTaskObject(sourceTaskName, "source"));
        defaultTaskObjectList.add(new DefaultTaskObject(javaDocTaskName, "javadoc"));
        return defaultTaskObjectList;
    }


    private static DefaultPublicationObject queueAppropriateMavenPublications(Project project, List<DefaultTaskObject> appropriateTasks, boolean hasMavenPublishPlugin) {
        String publicationName = Constants.PublicationName.apply(project.getName());

        if (hasMavenPublishPlugin) {
            PublicationContainer publicationContainer = project.getExtensions().getByType(DefaultPublishingExtension.class).getPublications();
            List<DefaultMavenPublication> mavenPublications = publicationContainer.stream()
                    .filter(publication -> publication instanceof DefaultMavenPublication)
                    .map(publication -> (DefaultMavenPublication) publication)
                    .collect(Collectors.toList());

            for (DefaultMavenPublication publication : mavenPublications) {
                while (publication.getName().equals(publicationName)) {
                    publicationName = publicationName + System.currentTimeMillis();
                }
            }
        }

        return new DefaultPublicationObject(publicationName, appropriateTasks);
    }

}
