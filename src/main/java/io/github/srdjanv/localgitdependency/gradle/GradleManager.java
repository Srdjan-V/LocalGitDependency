package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import org.eclipse.jgit.util.sha1.SHA1;
import org.gradle.tooling.*;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.gradle.util.GradleVersion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class GradleManager extends ManagerBase implements IGradleManager {
    private final Map<String, DefaultGradleConnector> gradleConnectorCache = new HashMap<>();

    GradleManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    private void cleanCache() {
        ManagerLogger.info("Clearing gradle connector cache");
        gradleConnectorCache.clear();
    }

    private DefaultGradleConnector getGradleConnector(Dependency dependency) {
        DefaultGradleConnector gradleConnector = gradleConnectorCache.get(dependency.getName());
        if (gradleConnector == null) {
            gradleConnector = (DefaultGradleConnector) GradleConnector.newConnector();
            gradleConnector.searchUpwards(false);
            gradleConnector.daemonMaxIdleTime(dependency.getGradleInfo().getGradleDaemonMaxIdleTime(), TimeUnit.SECONDS);
            gradleConnector.forProjectDirectory(dependency.getGitInfo().getDir());
            gradleConnectorCache.put(dependency.getName(), gradleConnector);
        }
        return gradleConnector;
    }

    @Override
    public void initGradleAPI() {
        validateMainInitScript();
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (!dependency.getPersistentInfo().isValidModel()) {
                runStartupTasks(dependency);
                probeProject(dependency);
            }

            validateDependencyInitScript(dependency);
        }
    }

    @Override
    public void buildDependencies() {
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (dependency.getGitInfo().hasRefreshed() || dependency.getPersistentInfo().hasDependencyTypeChanged()
                    || !dependency.getPersistentInfo().getBuildStatus()) {
                buildDependency(dependency);
            }
        }
        cleanCache();
    }

    @Override
    public void buildDependency(Dependency dependency) {
        runStartupTasks(dependency);

        long start = System.currentTimeMillis();
        ManagerLogger.info("Started building dependency: {}", dependency.getName());

        switch (dependency.getDependencyType()) {
            case Jar:
            case JarFlatDir:
                buildGradleProject(dependency, "build");
                break;

            case MavenLocal:
                buildGradleProject(dependency,
                        Constants.PublicationTaskName.apply(dependency.getPersistentInfo().getProbeData().getPublicationData()));
                break;

            case MavenProjectDependencyLocal:
            case MavenProjectLocal:
                PublicationData publicationData = dependency.getPersistentInfo().getProbeData().getPublicationData();
                buildGradleProject(dependency,
                        Constants.FilePublicationTaskName.apply(publicationData));
                break;

            default:
                throw new IllegalStateException();
        }

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Finished building in {} ms", spent);
    }

    @Override
    public void probeProject(Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started probing dependency: {} for information", dependency.getName());

        File initScriptFolder = getPropertyManager().getPluginConfig().getPersistentDir();
        File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyJsonInfoModel> customModelBuilder = connection.model(LocalGitDependencyJsonInfoModel.class);
            customModelBuilder.withArguments("--init-script", mainInit.getAbsolutePath());
            LocalGitDependencyJsonInfoModel jsonInfoModel = customModelBuilder.get();
            dependency.getPersistentInfo().setProbeData(jsonInfoModel.getJson());
        }

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Probe finished in {} ms", spent);
    }

    public void runStartupTasks(Dependency dependency) {
        if (dependency.getPersistentInfo().getRunStatus()) {
            if (!dependency.getGradleInfo().getLaunchers().getStartup().isExplicit()) {
                return;
            }
        }

        if (dependency.getGradleInfo().getLaunchers().getStartup().getPreTasks().isEmpty()) {
            dependency.getPersistentInfo().setStartupTasksStatus(true);
            return;
        }

        long start = System.currentTimeMillis();
        ManagerLogger.info("Started startupTasksRun for dependency: {}", dependency.getName());
        ManagerLogger.info("Tasks: {}", dependency.getGradleInfo().getLaunchers().getStartup().getPreTasks());

        runGradle(dependency,
                build -> {
                    build.forTasks(dependency.getGradleInfo().getLaunchers().getStartup().getPreTasks().toArray(new String[]{}));
                },
                startupTasksStatusResultHandler
        );

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("startupTasksRun finished in {} ms", spent);
    }

    private void buildGradleProject(Dependency dependency, String task) {
        runGradle(dependency,
                build -> {
                    build.withArguments("--init-script", dependency.getGradleInfo().getInitScript().getAbsolutePath());
                    build.forTasks(task);
                }, buildStatusResultHandler
        );
    }

    private void runGradle(Dependency dependency, Consumer<BuildLauncher> buildConfigurator, Function<Dependency, ResultHandler<Void>> function) {
        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            buildConfigurator.accept(build);
            build.setStandardOutput(System.out);
            build.setStandardError(System.err);
            // TODO: 23/05/2023  
/*            if (dependency.getGradleInfo().getJavaHome() != null) { 
                build.setJavaHome(dependency.getGradleInfo().getJavaHome());
            }*/
            build.run(function.apply(dependency));
        }
    }

    private String createDependencyInitScript(Dependency dependency) {
        // TODO: 18/02/2023 work on this
        ProjectProbeData data = dependency.getPersistentInfo().getProbeData();
        var gradleVersion = GradleVersion.version(data.getProjectGradleVersion());

        final Consumer<GradleInit> configuration;
        if (gradleVersion.compareTo(GradleVersion.version("6.0")) >= 0) {
            configuration = gradleInit -> gradleInit.setJavaJars(jars -> {
                if (dependency.getGradleInfo().isTryGeneratingSourceJar() &&
                        data.isCanProjectUseWithSourcesJar()) {
                    jars.add(GradleInit.JavaJars.sources());
                }
                if (dependency.getGradleInfo().isTryGeneratingJavaDocJar() &&
                        data.isCanProjectUseWithJavadocJar()) {
                    jars.add(GradleInit.JavaJars.javadoc());
                }
            });
        } else {
            PublicationData publicationObject = dependency.getPersistentInfo().getProbeData().getPublicationData();
            List<GradleInit.Task> tasks = new ArrayList<>();
            for (final String taskName : publicationObject.getTasks()) {
                TaskData taskData = dependency.getPersistentInfo().getProbeData().getArtifactTasks().
                        stream().filter(taskData1 -> taskData1.getName().equals(taskName)).
                        findAny().orElseThrow(IllegalStateException::new);

                switch (taskData.getClassifier()) {
                    case "sources":
                        if (dependency.getGradleInfo().isTryGeneratingSourceJar()) {
                            tasks.add(new GradleInit.Task(taskData.getName(), "sourceSets.main.allJava", taskData.getClassifier(), true));
                        }
                        break;

                    case "javadoc":
                        if (dependency.getGradleInfo().isTryGeneratingJavaDocJar()) {
                            tasks.add(new GradleInit.Task(taskData.getName(), "sourceSets.main.allJava", taskData.getClassifier(), true));
                        }
                }
            }

            configuration = gradleInit -> gradleInit.setTasks(t -> t.addAll(tasks));
        }

        switch (dependency.getDependencyType()) {
            case MavenProjectDependencyLocal:
            case MavenProjectLocal:
            case MavenLocal:
                return generateMavenInitScript(dependency, configuration);

            case JarFlatDir:
            case Jar:
                return generateJarInitScript(configuration);

            default:
                throw new IllegalStateException();
        }
    }

    private String generateMavenInitScript(Dependency dependency, Consumer<GradleInit> configuration) {
        List<Consumer<GradleInit>> configurations = new ArrayList<>();
        configurations.add(gradleInit -> gradleInit.setPlugins(pluginsList -> {
            pluginsList.add(GradleInit.Plugins.java());
            pluginsList.add(GradleInit.Plugins.mavenPublish());
        }));

        configurations.add(configuration);
        configurations.add(gradleInit -> {
            gradleInit.setPublishing(p -> p.add(new GradleInit.Publication(
                    dependency.getPersistentInfo().getProbeData().getPublicationData().getRepositoryName(),
                    dependency.getMavenFolder(),
                    dependency.getPersistentInfo().getProbeData().getPublicationData().getPublicationName())));
        });

        return GradleInit.crateInitProject(configurations);
    }

    private String generateJarInitScript(Consumer<GradleInit> configuration) {
        List<Consumer<GradleInit>> configurations = new ArrayList<>();
        configurations.add(gradleInit -> gradleInit.setPlugins(pluginsList -> {
            pluginsList.add(GradleInit.Plugins.java());
        }));
        configurations.add(configuration);

        return GradleInit.crateInitProject(configurations);
    }

    private void validateDependencyInitScript(Dependency dependency) {
        validateScript(
                dependency.getGradleInfo().getInitScript(),
                dependency.getGradleInfo().isKeepInitScriptUpdated(),
                () -> createDependencyInitScript(dependency),
                dependency.getPersistentInfo()::getInitFileSHA1,
                dependency.getPersistentInfo()::setInitFileSHA1);
    }

    private void validateMainInitScript() {
        PluginConfig globalProperty = getPropertyManager().getPluginConfig();
        File mainInit = Constants.concatFile.apply(globalProperty.getPersistentDir(), Constants.MAIN_INIT_SCRIPT_GRADLE);
        validateScript(
                mainInit,
                globalProperty.getKeepMainInitScriptUpdated(),
                GradleInit::createInitProbe,
                getPersistenceManager()::getInitScriptSHA,
                getPersistenceManager()::setInitScriptSHA);
    }

    private void validateScript(File file, boolean keepUpdated, Supplier<String> scriptSupplier, Supplier<String> persistentSHASupplier, Consumer<String> persistentSHASetter) {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new RuntimeException(String.format("This path: '%s' leads to a folder, it must be a file", file.getAbsolutePath()));
            }

            if (keepUpdated) {
                final String fileInitScriptSHA = generateShaForFile(file);
                final String persistentInitScriptSHA = persistentSHASupplier.get();

                if (!fileInitScriptSHA.equals(persistentInitScriptSHA)) {
                    ManagerLogger.info("File {}, contains local changes, updating file", file.getName());
                    final String initScript = scriptSupplier.get();
                    writeToFile(file, initScript);
                    persistentSHASetter.accept(generateShaForString(initScript));
                    return;
                }

                final String initScript = scriptSupplier.get();
                final String targetInitScriptSHA = generateShaForString(initScript);

                if (!fileInitScriptSHA.equals(targetInitScriptSHA)) {
                    ManagerLogger.info("Updating file {}", file.getName());
                    writeToFile(file, initScript);
                    persistentSHASetter.accept(targetInitScriptSHA);
                }
            }
        } else {
            ManagerLogger.info("Creating {}", file.getName());
            final String initScript = scriptSupplier.get();
            persistentSHASetter.accept(generateShaForString(initScript));
            writeToFile(file, initScript);
        }
    }

    private String generateShaForFile(File file) {
        SHA1 sha1 = SHA1.newInstance();
        byte[] buffer = new byte[4096];
        int read;

        try (FileInputStream inputStream = new FileInputStream(file)) {
            while ((read = inputStream.read(buffer)) > 0) {
                sha1.update(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Error while checking %s file integrity", file.getName()), e);
        }

        return sha1.toObjectId().getName();
    }

    private String generateShaForString(String script) {
        SHA1 sha1 = SHA1.newInstance();
        sha1.update(script.getBytes(StandardCharsets.UTF_8));
        return sha1.toObjectId().getName();
    }

    private void writeToFile(File file, String text) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            bufferedOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final Function<Dependency, ResultHandler<Void>> startupTasksStatusResultHandler = dependency -> {
        return new ResultHandler<>() {
            @Override
            public void onComplete(Void result) {
                dependency.getPersistentInfo().setStartupTasksStatus(true);
            }

            @Override
            public void onFailure(GradleConnectionException failure) {
                dependency.getPersistentInfo().setStartupTasksStatus(false);
                throw failure;
            }
        };
    };

    private static final Function<Dependency, ResultHandler<Void>> buildStatusResultHandler = dependency -> {
        return new ResultHandler<>() {
            @Override
            public void onComplete(Void result) {
                dependency.getPersistentInfo().setBuildStatus(true);
            }

            @Override
            public void onFailure(GradleConnectionException failure) {
                dependency.getPersistentInfo().setBuildStatus(false);
                throw failure;
            }
        };
    };
}

