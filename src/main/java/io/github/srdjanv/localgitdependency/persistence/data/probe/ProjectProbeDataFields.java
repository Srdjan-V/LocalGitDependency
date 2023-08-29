package io.github.srdjanv.localgitdependency.persistence.data.probe;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import java.util.List;
import org.gradle.api.JavaVersion;

class ProjectProbeDataFields {
    @NullableData
    String pluginVersion;

    String projectId;
    String archivesBaseName;
    String projectGradleVersion;
    JavaVersion javaVersion;

    @NullableData
    Boolean canProjectUseWithSourcesJar;

    @NullableData
    Boolean canProjectUseWithJavadocJar;

    List<SourceSetData> sourceSetsData;
    List<SubDependencyData> subDependencyData;
}
