package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
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
    Boolean canProjectUseWithSourcesJar;
    Boolean canProjectUseWithJavadocJar;
    PublicationData publicationData;
    List<TaskData> artifactTasks;
    List<SourceSetData> sourceSetsData;
}
