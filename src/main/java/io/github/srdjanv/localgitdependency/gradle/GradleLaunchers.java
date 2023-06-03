package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BuildConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.Launcher;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.ProbeConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.StartupConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import io.github.srdjanv.localgitdependency.persistence.data.probe.publicationdata.PublicationData;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BaseLauncherConfig;

public final class GradleLaunchers {
    public static GradleLaunchers build(Managers managers, DependencyConfig dependencyConfig, ErrorUtil errorBuilder) {
        Launcher.Builder builder = new Launcher.Builder();
        if (ClosureUtil.delegateNullSafe(dependencyConfig.getLauncher(), builder)) {
            return new GradleLaunchers(managers, dependencyConfig, new Launcher(builder), errorBuilder);
        }
        errorBuilder.append("DependencyConfig: 'buildLauncher' is null");
        return null;
    }

    private final File executable;
    private final Integer gradleDaemonMaxIdleTime;
    private final Startup startup;
    private final Probe probe;
    private final Build build;

    private GradleLaunchers(Managers managers, DependencyConfig dependencyConfig, Launcher launcher, ErrorUtil errorBuilder) {
        this.executable = launcher.getExecutable();
        if (launcher.getGradleDaemonMaxIdleTime() == null) {
            errorBuilder.append("BuildLauncher: 'gradleDaemonMaxIdleTime' is null");
            this.gradleDaemonMaxIdleTime = 0;
        } else this.gradleDaemonMaxIdleTime = launcher.getGradleDaemonMaxIdleTime();

        StartupConfig.Builder startupConfigBuilder = new StartupConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getStartup(), startupConfigBuilder)) {
            startup = new Startup(new StartupConfig(startupConfigBuilder), errorBuilder);
        } else {
            errorBuilder.append("BuildLauncher: 'startup' is null");
            startup = null;
        }

        ProbeConfig.Builder probeConfigBuilder = new ProbeConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getProbe(), probeConfigBuilder)) {
            probe = new Probe(new ProbeConfig(probeConfigBuilder), errorBuilder);
        } else {
            errorBuilder.append("BuildLauncher: 'probe' is null");
            probe = null;
        }

        BuildConfig.Builder buildConfigBuilder = new BuildConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getBuild(), buildConfigBuilder)) {
            build = new Build(new BuildConfig(buildConfigBuilder), errorBuilder);
        } else {
            errorBuilder.append("BuildLauncher: 'build' is null");
            build = null;
        }
    }

    @Nullable
    public File getExecutable() {
        return executable;
    }

    @NotNull
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    @NotNull
    public Startup getStartup() {
        return startup;
    }

    @NotNull
    public Probe getProbe() {
        return probe;
    }

    @NotNull
    public Build getBuild() {
        return build;
    }

    public static class Startup extends Base {
        private Startup(StartupConfig config, ErrorUtil errorBuilder) {
            super(config, errorBuilder);
        }

        @Override
        protected String customArgumentsWarning() {
            return null;
        }

        @Override
        protected BiFunction<Managers, Dependency, List<String>> defaultMainTasks(BaseLauncherConfig config) {
            return (managers, dep) -> Collections.emptyList();
        }

        @Override
        protected List<String> defaultTriggers() {
            return Collections.emptyList();
        }
    }

    public static class Probe extends Base {

        private Probe(ProbeConfig config, ErrorUtil errorBuilder) {
            super(config, errorBuilder);
        }

        @Override
        protected String customArgumentsWarning() {
            return null;
        }

        @Override
        protected BiFunction<Managers, Dependency, List<String>> defaultMainTasks(BaseLauncherConfig config) {
            return (manager, dep) -> Collections.emptyList();
        }

        @Override
        protected List<String> defaultTriggers() {
            List<String> triggers = new ArrayList<>();
            triggers.add("settings.gradle");
            triggers.add("build.gradle");
            triggers.add("gradle.properties");
            return triggers;
        }
    }


    public static class Build extends Base {
        private Build(BuildConfig config, ErrorUtil errorBuilder) {
            super(config, errorBuilder);
        }

        @Override
        protected String customArgumentsWarning() {
            return "Custom main tasks arguments detected, this is not recommended";
        }

        @Override
        protected BiFunction<Managers, Dependency, List<String>> defaultMainTasks(BaseLauncherConfig config) {
            if (config.getMainTasks() != null) {
                PluginLogger.warn("Custom main tasks detected, this is not recommended");
                final List<String> main = Arrays.asList(config.getMainTasks());
                return (manager, dep) -> main;
            } else {
                return (manager, dep) -> {
                    switch (dep.getDependencyType()) {
                        case Jar:
                        case JarFlatDir:
                            return Arrays.asList("build");

                        case MavenLocal:
                            return Arrays.asList(Constants.PublicationTaskName.apply(dep.getPersistentInfo().getProbeData().getPublicationData()));

                        case MavenProjectDependencyLocal:
                        case MavenProjectLocal:
                            PublicationData publicationData = dep.getPersistentInfo().getProbeData().getPublicationData();
                            return Arrays.asList(Constants.FilePublicationTaskName.apply(publicationData));

                        default:
                            throw new IllegalStateException();
                    }
                };
            }
        }

        @Override
        protected List<String> defaultTriggers() {
            List<String> triggers = new ArrayList<>();
            triggers.add("settings.gradle");
            triggers.add("build.gradle");
            triggers.add("gradle.properties");
            return triggers;
        }
    }

    public static abstract class Base {
        private final boolean explicit;
        private final BiFunction<Managers, Dependency, List<String>> preTasksArguments;
        private final List<String> preTasks;
        private final BiFunction<Managers, Dependency, List<String>> mainTasksArguments;
        private final BiFunction<Managers, Dependency, List<String>> mainTasks;
        private final BiFunction<Managers, Dependency, List<String>> postTasksArguments;
        private final List<String> postTasks;
        private final List<String> taskTriggers;
        private final boolean forwardOutput;

        public Base(BaseLauncherConfig config, ErrorUtil errorBuilder) {
            this.explicit = config.getExplicit() != null && config.getExplicit();

            this.preTasksArguments = defaultArguments(config, config.getPreTasksArguments());
            this.preTasks = getTasks(config.getPreTasks());

            this.mainTasksArguments = defaultArguments(config, config.getMainTasksArguments());
            this.mainTasks = defaultMainTasks(config);

            this.postTasksArguments = defaultArguments(config, config.getPostTasksArguments());
            this.postTasks = getTasks(config.getPostTasks());

            if (config.getSetTaskTriggers() != null && config.getSetTaskTriggers().length > 0) {
                taskTriggers = Arrays.asList(config.getSetTaskTriggers());
            } else if (config.getAddTaskTriggers() != null && config.getAddTaskTriggers().length > 0) {
                List<String> triggers = defaultTriggers();
                triggers.addAll(Arrays.asList(config.getAddTaskTriggers()));
                taskTriggers = (List<String>) Collections.unmodifiableCollection(triggers);
            } else {
                taskTriggers = Collections.emptyList();
            }

            if (config.getForwardOutput() == null) {
                errorBuilder.append("Launcher: forwardOutput is null");
                forwardOutput = false;
            } else forwardOutput = config.getForwardOutput();
        }

        protected List<String> getTasks(@Nullable String[] tasks) {
            return tasks == null ? Collections.emptyList() : Arrays.asList(tasks);
        }

        private BiFunction<Managers, Dependency, List<String>> defaultArguments(BaseLauncherConfig config, String[] argsSup) {
            if (config.getMainTasksArguments() == null) {
                return (managers, dep) -> {
                    final var warning = customArgumentsWarning();
                    if (warning != null) {
                        PluginLogger.warn(warning);
                    }

                    File initScriptFolder = managers.getPropertyManager().getPluginConfig().getPersistentDir();
                    File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);
                    return Arrays.asList("--init-script", mainInit.getAbsolutePath());
                };
            } else {
                final List<String> args = Arrays.asList(config.getMainTasksArguments());
                return (managers, dep) -> args;
            }
        }

        protected abstract String customArgumentsWarning();

        protected abstract BiFunction<Managers, Dependency, List<String>> defaultMainTasks(BaseLauncherConfig config);

        protected abstract List<String> defaultTriggers();

        public boolean isExplicit() {
            return explicit;
        }

        @NotNull
        public List<String> getPostTasksArguments(Managers managers, Dependency dependency) {
            return postTasksArguments.apply(managers, dependency);
        }

        @NotNull
        public List<String> getPreTasks() {
            return preTasks;
        }

        @NotNull
        public List<String> getMainTasksArguments(Managers managers, Dependency dependency) {
            return mainTasksArguments.apply(managers, dependency);
        }

        @NotNull
        public List<String> getMainTasks(Managers managers, Dependency dependency) {
            return mainTasks.apply(managers, dependency);
        }

        @NotNull
        public List<String> getPreTasksArguments(Managers managers, Dependency dependency) {
            return preTasksArguments.apply(managers, dependency);
        }

        @NotNull
        public List<String> getPostTasks() {
            return postTasks;
        }

        @NotNull
        public List<String> getTaskTriggers() {
            return taskTriggers;
        }

        public boolean isForwardOutput() {
            return forwardOutput;
        }
    }
}
