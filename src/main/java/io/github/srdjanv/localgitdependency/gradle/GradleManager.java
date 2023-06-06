package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.taskdata.TaskData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.eclipse.jgit.util.sha1.SHA1;
import org.gradle.tooling.*;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.gradle.util.GradleVersion;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            gradleConnector.daemonMaxIdleTime(dependency.getGradleInfo().getLaunchers().getGradleDaemonMaxIdleTime(), TimeUnit.SECONDS);
            gradleConnector.forProjectDirectory(dependency.getGitInfo().getDir());
            gradleConnectorCache.put(dependency.getName(), gradleConnector);
        }
        return gradleConnector;
    }

    @Override
    public void initGradleAPI() {
        validateMainInitScript();
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (checkForNeedlesTaskRunning(dependency,
                    GradleLaunchers::getStartup,
                    dep -> dep.getPersistentInfo().isSuccessfulStartup(),
                    info -> info.setStartupTasksStatus(true),
                    null)) {
                startStartupTasks(dependency);
            }

            if (checkForNeedlesTaskRunning(dependency,
                    GradleLaunchers::getProbe,
                    dep -> dep.getPersistentInfo().isSuccessfulProbe(),
                    info -> info.setProbeTasksStatus(true),
                    dep -> !dep.getPersistentInfo().isValidDataVersion())) {
                startProbeTasks(dependency);
            }

            validateDependencyInitScript(dependency);
        }
    }

    @Override
    public void startBuildTasks() {
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            if (checkForNeedlesTaskRunning(dependency,
                    GradleLaunchers::getBuild,
                    dep -> dep.getPersistentInfo().isSuccessfulBuild(),
                    info -> info.setBuildStatus(true),
                    dep -> dep.getPersistentInfo().hasDependencyTypeChanged())) {
                startBuildTasks(dependency);
            }
        }
        cleanCache();
    }

    private boolean checkForNeedlesTaskRunning(
            Dependency dependency,
            Function<GradleLaunchers, GradleLaunchers.Base> launcher,
            Predicate<Dependency> persistentSuccessStatus,
            Consumer<PersistentInfo> statusUpdater,
            @Nullable Predicate<Dependency> customChecks
    ) {
        boolean checkTasks = false;
        if (launcher.apply(dependency.getGradleInfo().getLaunchers()).isExplicit()) {
            checkTasks = true;
        } else if (dependency.getGitInfo().hasRefreshed()) {
            checkTasks = true;
        } else if (launcher.apply(dependency.getGradleInfo().getLaunchers()).isRunNeeded()) {
            checkTasks = true;
        } else if (customChecks != null) {
            if (customChecks.test(dependency)) {
                checkTasks = true;
            }
        }

        if (checkTasks || !persistentSuccessStatus.test(dependency)) {
            final var targetLauncher = launcher.apply(dependency.getGradleInfo().getLaunchers());
            List<String> tasks = Stream.of(
                            targetLauncher.getPreTasks(),
                            targetLauncher.getMainTasks(this, dependency),
                            targetLauncher.getPostTasks()).
                    flatMap(List::stream).collect(Collectors.toList());

            if (tasks.size() == 0) {
                statusUpdater.accept(dependency.getPersistentInfo());
                return false;
            } else {
                return true;
            }
        }
        statusUpdater.accept(dependency.getPersistentInfo());
        return false;
    }

    @Override
    public void startStartupTasks(Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started startupTasksRun for dependency: {}", dependency.getName());

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                dependency.getGradleInfo().getLaunchers().getStartup().getPreTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getStartup().getPreTasks(),
                startupTasksStatusResultHandler);

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                dependency.getGradleInfo().getLaunchers().getStartup().getMainTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getStartup().getMainTasks(this, dependency),
                startupTasksStatusResultHandler);

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                dependency.getGradleInfo().getLaunchers().getStartup().getPostTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getStartup().getPostTasks(),
                startupTasksStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("startupTasksRun finished in {} ms", spent);
    }

    @Override
    public void startProbeTasks(final Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started probing dependency: {} for information", dependency.getName());


        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                dependency.getGradleInfo().getLaunchers().getProbe().getPreTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getProbe().getPreTasks(),
                probeTasksStatusResultHandler);

        runGradleModel(dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                dependency.getGradleInfo().getLaunchers().getProbe().getMainTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getProbe().getMainTasks(this, dependency)
        );


        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                dependency.getGradleInfo().getLaunchers().getProbe().getPostTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getProbe().getPostTasks(),
                probeTasksStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Probe finished in {} ms", spent);
    }

    @Override
    public void startBuildTasks(Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started building dependency: {}", dependency.getName());

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                dependency.getGradleInfo().getLaunchers().getBuild().getPreTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getBuild().getPreTasks(),
                buildStatusResultHandler);

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                dependency.getGradleInfo().getLaunchers().getBuild().getMainTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getBuild().getMainTasks(this, dependency),
                buildStatusResultHandler);

        runTaskStage(dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                dependency.getGradleInfo().getLaunchers().getBuild().getPostTasksArguments(this, dependency),
                dependency.getGradleInfo().getLaunchers().getBuild().getPostTasks(),
                buildStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Finished building in {} ms", spent);
    }

    private void runTaskStage(
            Dependency dependency,
            GradleLaunchers.Base baseLauncher,
            List<String> args,
            List<String> tasks,
            Function<Dependency, ResultHandler<Void>> function
    ) {
        if (tasks.size() == 0) return;

        final String[] arrArgs;
        if (args.size() != 0) {
            arrArgs = args.toArray(new String[0]);
            ManagerLogger.info("Args: {}", (Object) arrArgs);
        } else arrArgs = new String[0];

        final String[] arrTasks = tasks.toArray(new String[0]);
        ManagerLogger.info("Tasks: {}", (Object) arrTasks);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.withArguments(arrArgs);
            build.forTasks(arrTasks);
            if (dependency.getGradleInfo().getLaunchers().getExecutable() != null) {
                build.setJavaHome(dependency.getGradleInfo().getLaunchers().getExecutable());
            }
            if (baseLauncher.isForwardOutput()) {
                build.setStandardOutput(System.out);
                build.setStandardError(System.err);
            }
            build.run(function.apply(dependency));
        }
    }

    private void runGradleModel(
            Dependency dependency,
            GradleLaunchers.Base baseLauncher,
            List<String> args,
            List<String> tasks
    ) {
        if (tasks.size() == 0 && args.size() == 0) return;

        final String[] arrArgs = args.toArray(new String[0]);
        ManagerLogger.info("Args: {}", (Object) arrArgs);

        final String[] arrTasks = tasks.toArray(new String[0]);
        ManagerLogger.info("Tasks: {}", (Object) arrTasks);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyJsonInfoModel> customModelBuilder = connection.model(LocalGitDependencyJsonInfoModel.class);
            customModelBuilder.withArguments(arrArgs);
            customModelBuilder.forTasks(arrTasks);
            if (dependency.getGradleInfo().getLaunchers().getExecutable() != null) {
                customModelBuilder.setJavaHome(dependency.getGradleInfo().getLaunchers().getExecutable());
            }
            if (baseLauncher.isForwardOutput()) {
                customModelBuilder.setStandardOutput(System.out);
                customModelBuilder.setStandardError(System.err);
            }

            customModelBuilder.get(GradleManager.mainProbeTasksResultHandler.apply(dependency));
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
                globalProperty.getKeepInitScriptUpdated(),
                GradleInit::createInitProbe,
                getPersistenceManager()::getInitScriptSHA,
                getPersistenceManager()::setInitScriptSHA);
    }

    private void validateScript(
            File file,
            boolean keepUpdated,
            Supplier<String> scriptSupplier,
            Supplier<String> persistentSHASupplier,
            Consumer<String> persistentSHASetter
    ) {
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

    private static final Function<Dependency, ResultHandler<Void>> probeTasksStatusResultHandler = dependency -> {
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

    public static final Function<Dependency, ResultHandler<LocalGitDependencyJsonInfoModel>> mainProbeTasksResultHandler = dependency -> {
        return new ResultHandler<>() {
            @Override
            public void onComplete(LocalGitDependencyJsonInfoModel result) {
                dependency.getPersistentInfo().setProbeData(result.getJson());
                dependency.getPersistentInfo().setProbeTasksStatus(true);
            }

            @Override
            public void onFailure(GradleConnectionException failure) {
                dependency.getPersistentInfo().setProbeTasksStatus(false);
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

