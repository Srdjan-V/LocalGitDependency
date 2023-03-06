package com.srdjanv.localgitdependency.injection.model;

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
    List<TaskObject> getAppropriateTasks();
    PublishingObject getAppropriatePublication();
}
