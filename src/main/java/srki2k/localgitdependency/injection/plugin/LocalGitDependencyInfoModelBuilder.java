package srki2k.localgitdependency.injection.plugin;

import org.gradle.api.Project;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import srki2k.localgitdependency.injection.model.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        List<String> allJarTasksNames = new ArrayList<>();
        List<String> allPublicationsNames = new ArrayList<>();

        if (hasJavaPlugin) {
            queueJarTasks(project, allJarTasksNames);
        }

        if (hasMavenPublishPlugin) {
            queueMavenPublications(project, allPublicationsNames);
        }

        return new DefaultLocalGitDependencyInfoModel(
                project.getGroup() + ":" + project.getName() + ":" + project.getVersion(),
                project.getGradle().getGradleVersion(),
                hasJavaPlugin,
                hasMavenPublishPlugin,
                allJarTasksNames,
                allPublicationsNames);
    }

    private void queueJarTasks(Project project, List<String> jarTaskList) {
        for (Jar jarTask : project.getTasks().withType(Jar.class)) {
            jarTaskList.add(jarTask.getName());
        }
    }

    private void queueMavenPublications(Project project, List<String> jarTaskList) {
        PublicationContainer publicationContainer = project.getExtensions().getByType(DefaultPublishingExtension.class).getPublications();
        publicationContainer.stream().filter(publication -> publication instanceof DefaultMavenPublication).forEach(publication -> {
            jarTaskList.add(publication.getName());
        });
    }

}
