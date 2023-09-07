package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.plugin.LocalGitDependencyJsonInfoModelBuilder;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

public class InjectionTest {

    @Test
    void testInjectionPlugin() {
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
    void testFailOnIncompleteData() {
        var builder = new ProjectProbeData.Builder();
        builder.setPluginVersion(Constants.PLUGIN_VERSION);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> DataParser.projectProbeDataJson(builder.create()),
                "Invalid Json parsing");
    }
}
