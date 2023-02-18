package srki2k.localgitdependency.persistence;

import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.PublishingObject;
import srki2k.localgitdependency.injection.model.TaskObject;
import srki2k.localgitdependency.injection.model.imp.DefaultLocalGitDependencyInfoModel;

import java.util.ArrayList;
import java.util.List;

public class SerializableProperty {
    String workingDirSHA1;
    String initFileSHA1;
    Dependency.Type dependencyType;
    DependencyInfoModelSerializable projectProbe;

    public static class DependencyInfoModelSerializable {
        public long versionUID = DefaultLocalGitDependencyInfoModel.serialVersionUID;
        private final String projectId;
        private final String projectGradleVersion;
        private final boolean hasJavaPlugin;
        private final boolean hasMavenPublishPlugin;
        private final List<TaskObjectSerializable> appropriateTasks = new ArrayList<>();
        private final PublicationObjectSerializable publicationObject;

        public DependencyInfoModelSerializable(LocalGitDependencyInfoModel localGitDependencyInfoModel) {
            this.projectId = localGitDependencyInfoModel.getProjectId();
            this.projectGradleVersion = localGitDependencyInfoModel.projectGradleVersion();
            this.hasJavaPlugin = localGitDependencyInfoModel.hasJavaPlugin();
            this.hasMavenPublishPlugin = localGitDependencyInfoModel.hasMavenPublishPlugin();
            localGitDependencyInfoModel.getAppropriateTasks()
                    .forEach(taskObject -> {
                        appropriateTasks.add(new TaskObjectSerializable(taskObject));
                    });
            this.publicationObject = new PublicationObjectSerializable(localGitDependencyInfoModel.getAppropriatePublication());
        }

        public long getVersionUID() {
            return versionUID;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getProjectGradleVersion() {
            return projectGradleVersion;
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
