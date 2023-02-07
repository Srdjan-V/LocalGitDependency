package srki2k.localgitdependency.gradle;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GradleApiManager {
    private final Map<File, DefaultGradleConnector> gradleConnectorCache = new HashMap<>();

    public void disconnectAllGradleConnectors() {
        gradleConnectorCache.values().forEach(DefaultGradleConnector::disconnect);
    }

    private DefaultGradleConnector getGradleConnector(Dependency dependency) {
        DefaultGradleConnector gradleConnector = gradleConnectorCache.get(dependency.getDir());
        if (gradleConnector == null) {
            gradleConnector = (DefaultGradleConnector) GradleConnector.newConnector();
            gradleConnector.searchUpwards(false);
            gradleConnector.daemonMaxIdleTime(1, TimeUnit.MICROSECONDS);
            gradleConnector.forProjectDirectory(dependency.getDir());
            gradleConnectorCache.put(dependency.getDir(), gradleConnector);
        }
        return gradleConnector;
    }

    public void buildGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getInitScript().getAbsolutePath());
            build.forTasks("build");
            build.run();
        }
    }

    public void publishGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getInitScript().getAbsolutePath());
            // TODO: 07/02/2023 set generated publication
            build.forTasks("maven-publish");
            build.run();
        }
    }

    public void createDependecyInitScript(Dependency dependency) {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getInitScript();
        if (!initScriptFolder.exists()) {
            initScriptFolder.mkdir();
        }

        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyInfoModel> customModelBuilder = connection.model(LocalGitDependencyInfoModel.class);
            customModelBuilder.withArguments("--init-script", mainInit.getAbsolutePath());
            LocalGitDependencyInfoModel model = customModelBuilder.get();

        }
    }

    public void createMainInitScript() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getInitScript();

        // TODO: 07/02/2023 add a way to check file integrity 
        if (!initScriptFolder.exists()) {
            initScriptFolder.mkdir();
        }

        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);
        if (mainInit.exists() && mainInit.isFile()) {
            return;
        }

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(mainInit.toPath()))) {
            bufferedOutputStream.write(GradleInit.createInitProbe().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

