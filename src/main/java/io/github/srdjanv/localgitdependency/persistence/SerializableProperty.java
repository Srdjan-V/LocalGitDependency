package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.PublishingObject;
import io.github.srdjanv.localgitdependency.injection.model.SourceSet;
import io.github.srdjanv.localgitdependency.injection.model.TaskObject;
import org.gradle.api.JavaVersion;

import java.util.ArrayList;
import java.util.List;

public class SerializableProperty {
    String workingDirSHA1;
    String initFileSHA1;
    Dependency.Type dependencyType;
    DependencyInfoModelSerializable projectProbe;

    public static class DependencyInfoModelSerializable implements LocalGitDependencyInfoModel {
        public String version = Constants.PROJECT_VERSION;
        private final String projectId;
        private final String projectGradleVersion;
        private final JavaVersion javaVersion;
        private final boolean canProjectUseWithSourcesJar;
        private final boolean canProjectUseWithJavadocJar;
        private final boolean hasJavaPlugin;
        private final boolean hasMavenPublishPlugin;
        private final List<TaskObjectSerializable> appropriateTasks = new ArrayList<>();
        private final List<SourceSetSerializable> defaultSourceSets = new ArrayList<>();
        private final PublicationObjectSerializable publicationObject;

        public DependencyInfoModelSerializable(LocalGitDependencyInfoModel localGitDependencyInfoModel) {
            this.projectId = localGitDependencyInfoModel.getProjectId();
            this.projectGradleVersion = localGitDependencyInfoModel.projectGradleVersion();
            this.javaVersion = localGitDependencyInfoModel.getProjectJavaVersion();
            this.canProjectUseWithSourcesJar = localGitDependencyInfoModel.canProjectUseWithSourcesJar();
            this.canProjectUseWithJavadocJar = localGitDependencyInfoModel.canProjectUseWithJavadocJar();
            this.hasJavaPlugin = localGitDependencyInfoModel.hasJavaPlugin();
            this.hasMavenPublishPlugin = localGitDependencyInfoModel.hasMavenPublishPlugin();
            for (TaskObject appropriateTask : localGitDependencyInfoModel.getAppropriateTasks()) {
                appropriateTasks.add(new TaskObjectSerializable(appropriateTask));
            }
            this.publicationObject = new PublicationObjectSerializable(localGitDependencyInfoModel.getAppropriatePublication());
            for (SourceSet source : localGitDependencyInfoModel.getSources()) {
                defaultSourceSets.add(new SourceSetSerializable(source));
            }
        }

        @Override

        public String getProjectId() {
            return projectId;
        }

        @Override
        public String projectGradleVersion() {
            return projectGradleVersion;
        }

        @Override
        public JavaVersion getProjectJavaVersion() {
            return javaVersion;
        }

        @Override
        public boolean canProjectUseWithSourcesJar() {
            return canProjectUseWithSourcesJar;
        }

        @Override
        public boolean canProjectUseWithJavadocJar() {
            return canProjectUseWithJavadocJar;
        }

        @Override
        public boolean hasJavaPlugin() {
            return hasJavaPlugin;
        }

        @Override
        public boolean hasMavenPublishPlugin() {
            return hasMavenPublishPlugin;
        }

        @Override
        public List<TaskObjectSerializable> getAppropriateTasks() {
            return appropriateTasks;
        }

        @Override
        public PublicationObjectSerializable getAppropriatePublication() {
            return publicationObject;
        }

        @Override
        public List<SourceSetSerializable> getSources() {
            return defaultSourceSets;
        }
    }

    public static class TaskObjectSerializable implements TaskObject {
        private final String name;
        private final String classifier;

        public TaskObjectSerializable(TaskObject taskObject) {
            this.name = taskObject.getName();
            this.classifier = taskObject.getClassifier();
        }

        public String getName() {
            return name;
        }

        public String getClassifier() {
            return classifier;
        }
    }

    public static class SourceSetSerializable implements SourceSet {
        private final String name;
        private final List<String> sources;

        public SourceSetSerializable(SourceSet sourceSet) {
            this.name = sourceSet.getName();
            this.sources = sourceSet.getSources();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getSources() {
            return sources;
        }

    }

    public static class PublicationObjectSerializable implements PublishingObject {
        private final String repositoryName;
        private final String publicationName;
        private final List<TaskObjectSerializable> tasks = new ArrayList<>();

        public PublicationObjectSerializable(PublishingObject publishingObject) {
            this.repositoryName = publishingObject.getRepositoryName();
            this.publicationName = publishingObject.getPublicationName();
            publishingObject.getTasks()
                    .forEach(taskObject -> {
                        tasks.add(new TaskObjectSerializable(taskObject));
                    });
        }

        public String getRepositoryName() {
            return repositoryName;
        }

        public String getPublicationName() {
            return publicationName;
        }

        public List<TaskObjectSerializable> getTasks() {
            return tasks;
        }

    }
}
