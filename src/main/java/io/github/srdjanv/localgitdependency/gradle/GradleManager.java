package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultLaunchers;
import io.github.srdjanv.localgitdependency.config.plugin.PluginConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.depenency.Dependency.Type;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyJsonInfoModel;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import io.github.srdjanv.localgitdependency.util.VersionUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.eclipse.jgit.util.sha1.SHA1;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.tooling.*;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

final class GradleManager extends ManagerBase implements IGradleManager {
    private final Map<String, DefaultGradleConnector> gradleConnectorCache = new HashMap<>();

    GradleManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {}

    private void cleanCache() {
        gradleConnectorCache.clear();
    }

    private DefaultGradleConnector getGradleConnector(Dependency dependency) {
        DefaultGradleConnector gradleConnector = gradleConnectorCache.get(dependency.getName());
        if (gradleConnector == null) {
            gradleConnector = (DefaultGradleConnector) GradleConnector.newConnector();
            gradleConnector.searchUpwards(false);
            gradleConnector.daemonMaxIdleTime(
                    dependency.getGradleInfo().getLaunchers().getGradleDaemonMaxIdleTime(), TimeUnit.SECONDS);
            gradleConnector.forProjectDirectory(dependency.getGitInfo().getDir());
            gradleConnectorCache.put(dependency.getName(), gradleConnector);
        }
        return gradleConnector;
    }

    @Override
    public void initGradleAPI() {
        final var deps = getDependencyManager().getDependencies();
        if (deps.isEmpty()) return;
        validateMainInitScript();
        for (Dependency dependency : deps) {
            if (checkForNeedlesTaskRunning(
                    dependency,
                    GradleLaunchers::getStartup,
                    dep -> dep.getPersistentInfo().isSuccessfulStartup(),
                    info -> info.setStartupTasksStatus(true),
                    null)) {
                startStartupTasks(dependency);
            }

            if (checkForNeedlesTaskRunning(
                    dependency,
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
            if (checkForNeedlesTaskRunning(
                    dependency,
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
            Function<GradleLaunchers, DefaultLaunchers.Base> launcher,
            Predicate<Dependency> persistentSuccessStatus,
            Consumer<PersistentInfo> statusUpdater,
            @Nullable Predicate<Dependency> customChecks) {
        boolean checkTasks = false;
        if (launcher.apply(dependency.getGradleInfo().getLaunchers())
                .getExplicit()
                .get()) {
            checkTasks = true;
        } else if (dependency.getGitInfo().hasRefreshed()) {
            checkTasks = true;
        } else if (launcher.apply(dependency.getGradleInfo().getLaunchers())
                .getIsRunNeeded()
                .getOrElse(false)) {
            checkTasks = true;
        } else if (customChecks != null) {
            if (customChecks.test(dependency)) {
                checkTasks = true;
            }
        }

        if (checkTasks || !persistentSuccessStatus.test(dependency)) {
            final var targetLauncher = launcher.apply(dependency.getGradleInfo().getLaunchers());
            if (targetLauncher instanceof Launchers.Probe) return true;
            if (targetLauncher instanceof Launchers.Build
                    && targetLauncher.getExplicit().get()) return true;

            List<String> tasks = Stream.of(
                            targetLauncher.getPreTasks(), targetLauncher.getMainTasks(), targetLauncher.getPostTasks())
                    .map(Provider::get)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (tasks.isEmpty()) {
                statusUpdater.accept(dependency.getPersistentInfo());
                return false;
            } else return true;
        }
        statusUpdater.accept(dependency.getPersistentInfo());
        return false;
    }

    @Override
    public void startStartupTasks(Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started startupTasksRun for dependency: {}", dependency.getName());

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                Launchers.Base::getPreTasksArguments,
                Launchers.Base::getPreTasks,
                startupTasksStatusResultHandler);

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                Launchers.Base::getMainTasksArguments,
                Launchers.Base::getMainTasks,
                startupTasksStatusResultHandler);

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getStartup(),
                Launchers.Base::getPostTasksArguments,
                Launchers.Base::getPostTasks,
                startupTasksStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("startupTasksRun finished in {} ms", spent);
    }

    @Override
    public void startProbeTasks(final Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started probing dependency: {} for information", dependency.getName());

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                Launchers.Base::getPreTasksArguments,
                Launchers.Base::getPreTasks,
                probeTasksStatusResultHandler);

        runGradleModel(
                dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                Launchers.Base::getMainTasksArguments,
                Launchers.Base::getMainTasks);

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getProbe(),
                Launchers.Base::getPostTasksArguments,
                Launchers.Base::getPostTasks,
                probeTasksStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Probe finished in {} ms", spent);
    }

    @Override
    public void startBuildTasks(Dependency dependency) {
        long start = System.currentTimeMillis();
        ManagerLogger.info("Started building dependency: {}", dependency.getName());

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                Launchers.Base::getPreTasksArguments,
                Launchers.Base::getPreTasks,
                buildStatusResultHandler);

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                Launchers.Base::getMainTasksArguments,
                Launchers.Base::getMainTasks,
                buildStatusResultHandler);

        runTaskStage(
                dependency,
                dependency.getGradleInfo().getLaunchers().getBuild(),
                Launchers.Base::getPostTasksArguments,
                Launchers.Base::getPostTasks,
                buildStatusResultHandler);

        long spent = System.currentTimeMillis() - start;
        ManagerLogger.info("Finished building in {} ms", spent);
    }

    private <GL extends Launchers.Base> void runTaskStage(
            Dependency dependency,
            GL baseLauncher,
            Function<GL, ListProperty<String>> argsFunction,
            Function<GL, ListProperty<String>> tasksFunction,
            Function<Dependency, ResultHandler<Void>> function) {
        var tasks = tasksFunction.apply(baseLauncher).get();
        var args = argsFunction.apply(baseLauncher).get();
        if (tasks.isEmpty() && args.isEmpty()) return;

        final String[] arrArgs;
        if (!args.isEmpty()) {
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

            if (baseLauncher.getForwardOutput().get()) {
                build.setStandardOutput(new IndentingOutputStream(System.out));
                build.setStandardError(new IndentingOutputStream(System.err));
            }
            build.run(function.apply(dependency));
        }
    }

    private <GL extends Launchers.Base> void runGradleModel(
            Dependency dependency,
            GL baseLauncher,
            Function<GL, ListProperty<String>> argsFunction,
            Function<GL, ListProperty<String>> tasksFunction) {
        var tasks = tasksFunction.apply(baseLauncher).get();
        var args = argsFunction.apply(baseLauncher).get();
        if (tasks.isEmpty() && args.isEmpty()) return;

        final String[] arrArgs = args.toArray(new String[0]);
        ManagerLogger.info("Args: {}", (Object) arrArgs);

        final String[] arrTasks = tasks.toArray(new String[0]);
        ManagerLogger.info("Tasks: {}", (Object) arrTasks);

        DefaultGradleConnector connector = getGradleConnector(dependency);
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<LocalGitDependencyJsonInfoModel> customModelBuilder =
                    connection.model(LocalGitDependencyJsonInfoModel.class);
            customModelBuilder.withArguments(arrArgs);
            customModelBuilder.forTasks(arrTasks);
            if (dependency.getGradleInfo().getLaunchers().getExecutable() != null) {
                customModelBuilder.setJavaHome(
                        dependency.getGradleInfo().getLaunchers().getExecutable());
            }

            if (baseLauncher.getForwardOutput().get()) {
                customModelBuilder.setStandardOutput(new IndentingOutputStream(System.out));
                customModelBuilder.setStandardError(new IndentingOutputStream(System.err));
            }

            customModelBuilder.get(GradleManager.mainProbeTasksResultHandler.apply(dependency));
        }
    }

    private String createDependencyInitScript(Dependency dependency) {
        final var gradleVersion = GradleVersion.version(
                dependency.getPersistentInfo().getProbeData().getProjectGradleVersion());

        List<Consumer<GradleInit>> initScriptBuilder = new ArrayList<>();
        var tags = dependency.getBuildTags();

        if (gradleVersion.compareTo(GradleVersion.version("6.0")) >= 0) {
            if (tags.stream().anyMatch(tag -> Arrays.asList(Type.MavenLocal, Type.JarFlatDir, Type.Jar)
                    .contains(tag))) buildJavaJars(dependency, initScriptBuilder);

        } else {
            if (tags.stream()
                    .anyMatch(tag -> Arrays.asList(Type.JarFlatDir, Type.Jar).contains(tag)))
                buildTaskJars(dependency, initScriptBuilder);
        }

        var lgdPluginVersion = dependency.getPersistentInfo().getProbeData().getPluginVersion();
        if (Objects.nonNull(lgdPluginVersion)
                && VersionUtil.check("0.6.0", lgdPluginVersion, check -> check.equal() && check.greater())) {
            tagSubDeps(dependency, initScriptBuilder);
        }

        return GradleInit.crateInitProject(initScriptBuilder);
    }

    private void tagSubDeps(final Dependency dependency, final List<Consumer<GradleInit>> builder) {
        final var tags = getDependencyManager().getSubDepTags(dependency.getName());
        if (tags == null) return;
        builder.add(gradleInit -> gradleInit.configureSubDeps(deps -> {
            for (Map.Entry<String, Set<Type>> depTags : tags.entrySet()) {
                deps.add(new GradleInit.SubDep(depTags.getKey(), depTags.getValue()));
            }
        }));
    }

    private void buildJavaJars(final Dependency dependency, final List<Consumer<GradleInit>> builder) {
        builder.add(gradleInit -> gradleInit.configureJavaJars(jars -> {
            if (dependency.getGradleInfo().isTryGeneratingSourceJar()
                    && Boolean.TRUE.equals(
                            dependency.getPersistentInfo().getProbeData().isCanProjectUseWithSourcesJar()))
                jars.add(GradleInit.JavaJars.SOURCES);

            if (dependency.getGradleInfo().isTryGeneratingJavaDocJar()
                    && Boolean.TRUE.equals(
                            dependency.getPersistentInfo().getProbeData().isCanProjectUseWithJavadocJar()))
                jars.add(GradleInit.JavaJars.JAVADOC);
        }));
    }

    private void buildTaskJars(final Dependency dependency, final List<Consumer<GradleInit>> builder) {
        builder.add(gradleInit -> gradleInit.configureJarTasks(tasks -> {
            if (dependency.getGradleInfo().isTryGeneratingSourceJar()) tasks.add(GradleInit.JarTasks.SOURCES);

            if (dependency.getGradleInfo().isTryGeneratingJavaDocJar()) tasks.add(GradleInit.JarTasks.JAVADOC);
        }));
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
        PluginConfig pluginConfig = getConfigManager().getPluginConfig();
        File mainInit =
                FileUtil.concat(FileUtil.getLgdDir(getProject()).getAsFile(), Constants.MAIN_INIT_SCRIPT_GRADLE);
        validateScript(
                mainInit,
                pluginConfig.getKeepInitScriptUpdated().get(),
                GradleInit::createInitProbe,
                getPersistenceManager()::getInitScriptSHA,
                getPersistenceManager()::setInitScriptSHA);
    }

    private void validateScript(
            File file,
            boolean keepUpdated,
            Supplier<String> scriptSupplier,
            Supplier<String> persistentSHASupplier,
            Consumer<String> persistentSHASetter) {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new RuntimeException(
                        String.format("This path: '%s' leads to a folder, it must be a file", file.getAbsolutePath()));
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
        try (BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
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

    public static final Function<Dependency, ResultHandler<LocalGitDependencyJsonInfoModel>>
            mainProbeTasksResultHandler = dependency -> {
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

    static class IndentingOutputStream extends OutputStream {
        private final StringBuilder builder = new StringBuilder();
        private final Object out;
        private boolean previousWasCarriageReturn = false;

        public IndentingOutputStream(@NotNull List<String> list) {
            this.out = list;
        }

        public IndentingOutputStream(@NotNull PrintStream out) {
            this.out = out;
        }

        @Override
        public void write(int c) {
            if (c == '\n') {
                if (previousWasCarriageReturn) {
                    previousWasCarriageReturn = false;
                    return; // Skip if previous was a carriage return
                }
                processCurrentString();
            } else if (c == '\r') {
                previousWasCarriageReturn = true;
                processCurrentString();
            } else {
                previousWasCarriageReturn = false;
                builder.append((char) c);
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private void processCurrentString() {
            String s = builder.length() > 0
                    ? builder.insert(0, Constants.TAB_INDENT).toString()
                    : "";
            builder.setLength(0);
            if (out instanceof PrintStream printStream) {
                printStream.println(s);
            } else if (out instanceof List list) {
                list.add(s);
            } else throw new IllegalStateException();
        }
    }
}
