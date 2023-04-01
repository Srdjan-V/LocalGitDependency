package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.JavaVersion;

import java.util.List;

public interface ProjectProbeDataSetters {
    void setVersion(String version);
    void setProjectId(String projectId);
    void setProjectGradleVersion(String projectGradleVersion);
    void setJavaVersion(JavaVersion javaVersion);
    void setCanProjectUseWithSourcesJar(boolean canProjectUseWithSourcesJar);
    void setCanProjectUseWithJavadocJar(boolean canProjectUseWithJavadocJar);
    void setHasJavaPlugin(boolean hasJavaPlugin);
    void setHasMavenPublishPlugin(boolean hasMavenPublishPlugin);
    void setTaskData(List<TaskData> appropriateTasks);
    void setSourceSetData(List<SourceSetData> defaultSourceSets);
    void setPublicationData(PublicationData publicationData);
}
