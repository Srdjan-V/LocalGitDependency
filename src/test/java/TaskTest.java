import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import srki2k.localgitdependency.Instances;

import java.io.File;

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

        project.getPluginManager().apply("srki2k.local-git-dependency");
        project.getPluginManager().apply("java");
    }


    @Test
    void beforeAll() {
        Instances.getSettingsExtension().add("implementation", "https://github.com/Srdjan-V/TweakedLib.git");
        //Instances.getSettingsExtension().add("https://github.com/CleanroomMC/GroovyScript.git");
        Instances.getDependencyManager().registerGitDependencies();
        //Instances.getDependencyManager().buildDependencies(true);
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
