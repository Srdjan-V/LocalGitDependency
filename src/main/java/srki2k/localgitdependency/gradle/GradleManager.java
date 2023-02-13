package srki2k.localgitdependency.gradle;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.Instances;
import srki2k.localgitdependency.depenency.Dependency;
import srki2k.localgitdependency.injection.model.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GradleManager {
    private final Map<File, DefaultGradleConnector> gradleConnectorCache = new HashMap<>();

    public void disconnectAllGradleConnectors() {
        gradleConnectorCache.values().forEach(DefaultGradleConnector::disconnect);
    }

    private DefaultGradleConnector getGradleConnector(Dependency dependency) {
        DefaultGradleConnector gradleConnector = gradleConnectorCache.get(dependency.getGitInfo().getDir());
        if (gradleConnector == null) {
            gradleConnector = (DefaultGradleConnector) GradleConnector.newConnector();
            gradleConnector.searchUpwards(false);
            gradleConnector.daemonMaxIdleTime(1, TimeUnit.MICROSECONDS);
            gradleConnector.forProjectDirectory(dependency.getGitInfo().getDir());
            gradleConnectorCache.put(dependency.getGitInfo().getDir(), gradleConnector);
        }
        return gradleConnector;
    }

    public void initGradleAPI() {
        createMainInitScript();
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (!dependency.getPersistentInfo().isValidModel()) {
                probeProject(dependency);
            }

            if (!dependency.getGradleInfo().isGradleProbeCashing()) {
                createDependencyInitScript(dependency);
            }
        }
    }

    public void buildDependencies() {
        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            if (dependency.getGitInfo().hasRefreshed()) {
                switch (dependency.getDependencyType()) {
                    case Jar:
                        buildGradleProject(dependency);
                        break;

                    case MavenLocal:
                        publishGradleProject(dependency);
                }
            }
        }
    }

    private void probeProject(Dependency dependency) {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyInfoModel> customModelBuilder = connection.model(LocalGitDependencyInfoModel.class);
            customModelBuilder.withArguments("--init-script", mainInit.getAbsolutePath());
            LocalGitDependencyInfoModel localGitDependencyInfoModel = customModelBuilder.get();
            // TODO: 13/02/2023 crate a better bridge 
            DefaultLocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel = new DefaultLocalGitDependencyInfoModel(
                    localGitDependencyInfoModel.getProjectId(),
                    localGitDependencyInfoModel.projectGradleVersion(),
                    localGitDependencyInfoModel.hasJavaPlugin(),
                    localGitDependencyInfoModel.hasMavenPublishPlugin(),
                    localGitDependencyInfoModel.getAllJarTasksNames(),
                    null
            );
            dependency.getPersistentInfo().setDefaultLocalGitDependencyInfoModel(defaultLocalGitDependencyInfoModel);
        }
    }

    private void buildGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getGradleInfo().getInitScript().getAbsolutePath());
            build.forTasks("build");
            build.run();
        }
    }

    private void publishGradleProject(Dependency dependency) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments("--init-script", dependency.getGradleInfo().getInitScript().getAbsolutePath());
            // TODO: 07/02/2023 set generated publication
            build.forTasks("publishToMavenLocal");
            build.run();
        }
    }

    private void createDependencyInitScript(Dependency dependency) {
        DefaultLocalGitDependencyInfoModel model = dependency.getPersistentInfo().getDefaultLocalGitDependencyInfoModel();
        int[] gradleVersion = Arrays.stream(model.projectGradleVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();

        Consumer<List<GradleInit.Plugins>> plugins = pluginsList -> {
            pluginsList.add(GradleInit.Plugins.java());
            pluginsList.add(GradleInit.Plugins.mavenPublish());
        };

        String initFile;
        if (gradleVersion[0] >= 6 && gradleVersion[1] >= 0) {
            Consumer<GradleInit> configuration = gradleInit -> {
                gradleInit.setPlugins(plugins);
                gradleInit.setJavaJars(javaJars -> javaJars.add(GradleInit.JavaJars.sources()));
                gradleInit.setPublications(taskPublication -> taskPublication.add(new GradleInit.Publication(Constants.PublicationName.apply(dependency.getName()), null)));
            };
            initFile = GradleInit.crateInitProject(configuration);
        } else {
            GradleInit.Task sourceTask = new GradleInit.Task(Constants.JarTaskName.apply(dependency.getName()), "sourceSets.main.allJava", "source");
            GradleInit.Publication taskPublication = new GradleInit.Publication(Constants.PublicationName.apply(dependency.getName()), sourceTask);

            Consumer<GradleInit> configuration = gradleInit -> {
                gradleInit.setPlugins(plugins);
                gradleInit.setTasks(tasks -> tasks.add(sourceTask));
                gradleInit.setPublications(taskPublication1 -> taskPublication1.add(taskPublication));
            };
            initFile = GradleInit.crateInitProject(configuration);
        }
        writeToFile(dependency.getGradleInfo().getInitScript(), initFile);
    }


    private void createMainInitScript() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();

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

