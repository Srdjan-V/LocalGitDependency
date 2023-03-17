package io.github.srdjanv.localgitdependency;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ProjectInstance {

    public static Project createProject() {
        File projectDir = new File(".", "test/project/");
        File homeDir = new File(".", "test/gradle_home/");

        Project project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .withGradleUserHomeDir(homeDir)
                .build();

        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("gradle.properties"))) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        project.setVersion(properties.getProperty("version"));
        Constants.PROJECT_VERSION = properties.getProperty("version");
        project.setGroup(properties.getProperty("group"));
        project.getPluginManager().apply("io.github.srdjan-v.local-git-dependency");
        project.getPluginManager().apply("java");

        return project;
    }
}
