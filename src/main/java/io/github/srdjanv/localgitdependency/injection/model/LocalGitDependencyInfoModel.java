package io.github.srdjanv.localgitdependency.injection.model;

import org.gradle.api.JavaVersion;

import java.util.List;

@SuppressWarnings("unused")
public interface LocalGitDependencyInfoModel {
    String getProjectId();
    String projectGradleVersion();
    JavaVersion getProjectJavaVersion();
    boolean canProjectUseWithSourcesJar();
    boolean canProjectUseWithJavadocJar();
    boolean hasJavaPlugin();
    boolean hasMavenPublishPlugin();
    List<? extends TaskObject> getAppropriateTasks();
    PublishingObject getAppropriatePublication();
    List<? extends SourceSet> getSources();
}
