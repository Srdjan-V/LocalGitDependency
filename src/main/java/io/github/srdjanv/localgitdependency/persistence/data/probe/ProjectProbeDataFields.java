package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import org.gradle.api.JavaVersion;

import java.util.List;

class ProjectProbeDataFields {
    String version;
    String projectId;
    String archivesBaseName;
    String projectGradleVersion;
    JavaVersion javaVersion;
    boolean canProjectUseWithSourcesJar;
    boolean canProjectUseWithJavadocJar;
    boolean hasJavaPlugin;
    boolean hasMavenPublishPlugin;
    List<TaskData> taskData;
    List<SourceSetData> sourceSetsData;
    PublicationData publicationData;
    List<Repository> repositoryList;
}
