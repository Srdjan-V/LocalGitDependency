package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.gradle.api.JavaVersion;

import java.util.List;

class ProjectProbeDataFields {
    @NullableData
    String pluginVersion;
    String projectId;
    String archivesBaseName;
    String projectGradleVersion;
    JavaVersion javaVersion;
    Boolean canProjectUseWithSourcesJar;
    Boolean canProjectUseWithJavadocJar;
    PublicationData publicationData;
    List<TaskData> artifactTasks;
    List<SourceSetData> sourceSetsData;
    List<SubDependencyData> subDependencyData;
}
