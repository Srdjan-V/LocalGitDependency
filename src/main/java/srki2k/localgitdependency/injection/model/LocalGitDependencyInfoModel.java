package srki2k.localgitdependency.injection.model;

import java.util.List;

@SuppressWarnings("unused")
public interface LocalGitDependencyInfoModel {
    String getProjectId();
    String projectGradleVersion();
    boolean hasJavaPlugin();
    boolean hasMavenPublishPlugin();
    List<TaskObject> getAppropriateTasks();
    PublicationObject getAppropriatePublication();
}
