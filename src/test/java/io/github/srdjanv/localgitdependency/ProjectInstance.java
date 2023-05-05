package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyManagerInstance;
import io.github.srdjanv.localgitdependency.project.IProjectManager;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.*;
import java.util.Properties;

public class ProjectInstance {

    private static String branch;

    private static String getBranch() {
        if (branch == null) {
            try (InputStream stream = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD").getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                branch = bufferedReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return branch;
    }

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

        String version;
        if (!getBranch().equals("master")) {
            version = properties.getProperty("version") + "-dev";
        } else {
            version = properties.getProperty("version");
        }

        project.setVersion(version);
        Constants.PROJECT_VERSION = version;
        project.setGroup(properties.getProperty("group"));
        project.getPluginManager().apply("io.github.srdjan-v.local-git-dependency");
        project.getPluginManager().apply("java");

        return project;
    }

    public static IProjectManager getManager(Project project) {
        return project.getExtensions().findByType(LocalGitDependencyManagerInstance.class).getProjectManager();
    }
}
