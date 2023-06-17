package io.github.srdjanv.localgitdependency;

import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.extentions.LocalGitDependencyManagerInstance;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.injection.plugin.LocalGitDependencyJsonInfoModelBuilder;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
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

        var lgdInstance = project.getExtensions().getByType(LocalGitDependencyManagerInstance.class);
        var emptyUrl = "url";

        lgdInstance.getDependencyManager().
                registerDependency(Constants.JAVA_IMPLEMENTATION, emptyUrl,
                        ClosureUtil.<DependencyConfig.Builder>configure(builder -> {
                            builder.name("dep1");
                        }));

        lgdInstance.getDependencyManager().
                registerDependency(Constants.JAVA_IMPLEMENTATION, emptyUrl,
                        ClosureUtil.<DependencyConfig.Builder>configure(builder -> {
                            builder.name("dep2");
                            builder.dependencyType(Dependency.Type.MavenProjectLocal);
                        }));

        project.getDependencies().add(Constants.JAVA_IMPLEMENTATION, "org.jetbrains:annotations:24.0.1");
        project.getDependencies().add(Constants.JAVA_IMPLEMENTATION, "com.google.code.gson:gson:2.10.1");

        String json;
        {
            Object data = new LocalGitDependencyJsonInfoModelBuilder().buildAll(LocalGitDependencyJsonInfoModel.class.getName(), project);
            json = ((LocalGitDependencyJsonInfoModel) data).getJson();
        }

        ThrowingSupplier<ProjectProbeData> throwingSupplier = () -> DataParser.parseJson(json);
        ProjectProbeData data = Assertions.assertDoesNotThrow(throwingSupplier, "Invalid Json Data");

        Assertions.assertEquals(2, data.getSourceSetsData().get(0).getCompileClasspath().size());
    }

}
