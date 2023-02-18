package srki2k.localgitdependency.injection.model;

import java.util.List;

@SuppressWarnings("unused")
public interface LocalGitDependencyInfoModel { // TODO: 18/02/2023 add java version, withSourcesJar, withJavadocJar
    long serialVersionUID = 1L;
    String getProjectId();
    String projectGradleVersion();
    boolean hasJavaPlugin();
    boolean hasMavenPublishPlugin();
    List<TaskObject> getAppropriateTasks();
    PublishingObject getAppropriatePublication();
}
