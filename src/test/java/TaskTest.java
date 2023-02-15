import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import srki2k.localgitdependency.Instances;

import java.io.*;
import java.util.Properties;

public class TaskTest {

    static Project project;

    static {
        File projectDir = new File(".", "test/project/");
        new File(projectDir, "run").mkdirs();
        File homeDir = new File(".", "test/gradle_home/");

        project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .withGradleUserHomeDir(homeDir)
                .build();

        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("gradle.properties")))) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        project.setVersion(properties.getProperty("version"));
        project.setGroup(properties.getProperty("group"));
        project.getPluginManager().apply("srki2k.local-git-dependency");
        project.getPluginManager().apply("java");
    }


    @Test
    void beforeAll() {
        Instances.getSettingsExtension().add("implementation", "https://github.com/Srdjan-V/TweakedLib.git");
        Instances.getSettingsExtension().add("https://github.com/CleanroomMC/GroovyScript.git");
        Instances.getGitManager().initRepos();
        Instances.getGradleManager().initGradleAPI();
        Instances.getPersistenceManager().savePersistentData();
        Instances.getDependencyManager().addBuiltDependencies();
    }

    @Test
    void gradle() {
        Instances.getSettingsExtension().add("implementation", "https://github.com/Srdjan-V/TweakedLib.git");
        //var set = Instances.getDependencyManager().getDependencies();

   /*     set.forEach(dependency -> {
            GradleUtil.buildGradleProject(dependency);
        });*/
    }


}
