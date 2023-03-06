package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.injection.model.PublishingObject;
import io.github.srdjanv.localgitdependency.injection.model.TaskObject;
import org.gradle.api.JavaVersion;

import java.util.ArrayList;
import java.util.List;

public class SerializableProperty {
    String workingDirSHA1;
    String initFileSHA1;
    Dependency.Type dependencyType;
    DependencyInfoModelSerializable projectProbe;

    public static class DependencyInfoModelSerializable {
        public String version = Constants.PROJECT_VERSION;
        private final String projectId;
        private final String projectGradleVersion;
        private final JavaVersion javaVersion;
        private final boolean canProjectUseWithSourcesJar;
        private final boolean canProjectUseWithJavadocJar;
        private final boolean hasJavaPlugin;
        private final boolean hasMavenPublishPlugin;
        private final List<TaskObjectSerializable> appropriateTasks = new ArrayList<>();
        private final PublicationObjectSerializable publicationObject;

        public DependencyInfoModelSerializable(LocalGitDependencyInfoModel localGitDependencyInfoModel) {
            this.projectId = localGitDependencyInfoModel.getProjectId();
            this.projectGradleVersion = localGitDependencyInfoModel.projectGradleVersion();
            this.javaVersion = localGitDependencyInfoModel.getProjectJavaVersion();
            this.canProjectUseWithSourcesJar = localGitDependencyInfoModel.canProjectUseWithSourcesJar();
            this.canProjectUseWithJavadocJar = localGitDependencyInfoModel.canProjectUseWithJavadocJar();
            this.hasJavaPlugin = localGitDependencyInfoModel.hasJavaPlugin();
            this.hasMavenPublishPlugin = localGitDependencyInfoModel.hasMavenPublishPlugin();
            localGitDependencyInfoModel.getAppropriateTasks()
                    .forEach(taskObject -> {
                        appropriateTasks.add(new TaskObjectSerializable(taskObject));
                    });
            this.publicationObject = new PublicationObjectSerializable(localGitDependencyInfoModel.getAppropriatePublication());
        }

        public String getVersion() {
            return version;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getProjectGradleVersion() {
            return projectGradleVersion;
        }

        public JavaVersion getJavaVersion() {
            return javaVersion;
        }

        public boolean isCanProjectUseWithSourcesJar() {
            return canProjectUseWithSourcesJar;
        }

        public boolean isCanProjectUseWithJavadocJar() {
            return canProjectUseWithJavadocJar;
        }

        public boolean isHasJavaPlugin() {
            return hasJavaPlugin;
        }

        public boolean isHasMavenPublishPlugin() {
            return hasMavenPublishPlugin;
        }

        public List<TaskObjectSerializable> getAppropriateTasks() {
            return appropriateTasks;
        }

        public PublicationObjectSerializable getPublicationObject() {
            return publicationObject;
        }
    }

    public static class TaskObjectSerializable {
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

    public static class PublicationObjectSerializable {
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
