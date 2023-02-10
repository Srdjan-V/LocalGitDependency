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
import java.util.Arrays;
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

    public void createDependencyInitScript(Dependency dependency) {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getInitScript();
        if (!initScriptFolder.exists()) {
            initScriptFolder.mkdirs();
        }

        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyInfoModel> customModelBuilder = connection.model(LocalGitDependencyInfoModel.class);
            customModelBuilder.withArguments("--init-script", mainInit.getAbsolutePath());
            LocalGitDependencyInfoModel model = customModelBuilder.get();
            int[] gradleVersion = Arrays.stream(model.projectGradleVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();

            String initFile;
            if (gradleVersion[0] >= 6 && gradleVersion[1] >= 0) {
                initFile = GradleInit.crateInitProject(
                        new GradleInit.Plugins[]{GradleInit.Plugins.JAVA, GradleInit.Plugins.MAVEN_PUBLISH},
                        new GradleInit.JavaJars[]{GradleInit.JavaJars.SOURCES},
                        null,
                        null,
                        new GradleInit.Publishing(new GradleInit.Publication(Constants.PublicationName.apply(dependency.getName()), null))
                );
            } else {
                GradleInit.Task sourceTask = new GradleInit.Task(Constants.JarTaskName.apply(dependency.getName()), "sourceSets.main.allJava", "source");
                GradleInit.Publication taskPublication = new GradleInit.Publication(Constants.PublicationName.apply(dependency.getName()), sourceTask);

                initFile = GradleInit.crateInitProject(
                        new GradleInit.Plugins[]{GradleInit.Plugins.JAVA, GradleInit.Plugins.MAVEN_PUBLISH},
                        null,
                        new GradleInit.Task[]{sourceTask},
                        new GradleInit.Artifacts(sourceTask),
                        new GradleInit.Publishing(taskPublication)
                );
            }
            writeToFile(dependency.getInitScript().getAbsoluteFile(), initFile);
        }
    }

    public void createMainInitScript() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getInitScript();

        // TODO: 07/02/2023 add a way to check file integrity 
        if (!initScriptFolder.exists()) {
            initScriptFolder.mkdirs();
        }

        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);
        if (mainInit.exists() && mainInit.isFile()) {
            return;
        }

        writeToFile(mainInit, GradleInit.createInitProbe());
    }

    private void writeToFile(File file, String text) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            bufferedOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

