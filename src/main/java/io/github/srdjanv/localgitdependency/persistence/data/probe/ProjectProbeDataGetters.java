package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationDataGetters;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.IRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetDataGetters;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskDataGetters;
import org.gradle.api.JavaVersion;

import java.util.List;

public interface ProjectProbeDataGetters {
    String getVersion();
    String getProjectId();
    String getProjectGradleVersion();
    JavaVersion getJavaVersion();
    boolean isCanProjectUseWithSourcesJar();
    boolean isCanProjectUseWithJavadocJar();
    boolean isHasJavaPlugin();
    boolean isHasMavenPublishPlugin();
    List<? extends TaskDataGetters> getTaskData();
    List<? extends SourceSetDataGetters> getSourceSetData();
    PublicationDataGetters getPublicationData();
    List<? extends IRepository> getRepositoryList();
}
