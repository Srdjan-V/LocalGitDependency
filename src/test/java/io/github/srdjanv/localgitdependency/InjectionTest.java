package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.dependency.DependencyRegistry;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.plugin.LocalGitDependencyJsonInfoModelBuilder;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.project.BuildScriptGenerator;
import io.github.srdjanv.localgitdependency.project.ProjectInstance;
import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

public class InjectionTest {

    @Test
    void basicInjectionPluginTest() {
        Project project = ProjectInstance.createProject();

        project.getRepositories().mavenCentral();
        project.getRepositories().mavenLocal();
        project.getRepositories().gradlePluginPortal();

        project.getDependencies().add(Constants.JAVA_IMPLEMENTATION, "org.jetbrains:annotations:24.0.1");
        project.getDependencies().add(Constants.JAVA_IMPLEMENTATION, "com.google.code.gson:gson:2.10.1");

        String json;
        {
            Object data = new LocalGitDependencyJsonInfoModelBuilder()
                    .buildAll(LocalGitDependencyJsonInfoModel.class.getName(), project);
            json = ((LocalGitDependencyJsonInfoModel) data).getJson();
        }

        ThrowingSupplier<ProjectProbeData> throwingSupplier = () -> DataParser.parseJson(json);
        ProjectProbeData data = Assertions.assertDoesNotThrow(throwingSupplier, "Invalid Json Data");

        Assertions.assertEquals(
                2, data.getSourceSetsData().get(0).getCompileClasspath().size());
    }

    @Test
    void complexInjectionPluginTest() {
        var dep = DependencyRegistry.getTestDependency(id -> DependencyRegistry.Types.BRANCH
                .nameType(DependencyRegistry.getGradleBranch("8.0"))
                .equals(id));

        var repoBuilder = new BuildScriptGenerator.Repo();
        repoBuilder.append(
                """
                    mavenCentral()
                    mavenLocal()
                    gradlePluginPortal()
                """);

        var depBuilder = new BuildScriptGenerator.Deps();
        depBuilder.append(String.format(
                """
                    %s 'org.jetbrains:annotations:24.0.1'
                    %s 'com.google.code.gson:gson:2.10.1'
                """,
                Constants.JAVA_IMPLEMENTATION, Constants.JAVA_IMPLEMENTATION));

        var lgdBuilder = new BuildScriptGenerator.LDGDeps();
        lgdBuilder.append(
                """
                        register("https://github.com/Srdjan-V/LocalGitDependencyTestRepo.git") {
                            branch = "Gradle-8.0"
                        }
                    """);

        dep.setTestName("complexInjectionPluginTest");
        BuildScriptGenerator.generate(dep, repoBuilder, depBuilder, lgdBuilder);

        dep.registerDepToExtension(config -> config.getName().set(dep.getTestName()));
        dep.getProjectManager().startPlugin();
        var data = dep.getDependency().getPersistentInfo().getProbeData();

        Assertions.assertEquals(
                2, data.getSourceSetsData().get(0).getCompileClasspath().size());

        Assertions.assertEquals(1, data.getSubDependencyData().size());
    }

    @Test
    void basicFailOnIncompleteDataTest() {
        var builder = new ProjectProbeData.Builder();
        builder.setPluginVersion(Constants.PLUGIN_VERSION);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> DataParser.projectProbeDataJson(builder.create()),
                "Invalid Json parsing");
    }
}
