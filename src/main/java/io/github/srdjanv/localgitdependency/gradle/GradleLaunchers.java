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
    public static GradleLaunchers build(DependencyConfig dependencyConfig, ErrorUtil errorBuilder) {
        return new GradleLaunchers(
                dependencyConfig.getLauncher(),
                errorBuilder);
    }

    private final File executable;
    private final Integer gradleDaemonMaxIdleTime;
    private final Startup startup;
    private final Probe probe;
    private final Build build;

    private GradleLaunchers(Launcher launcher, ErrorUtil errorBuilder) {
        this.executable = launcher.getExecutable();
        if (launcher.getGradleDaemonMaxIdleTime() == null) {
            errorBuilder.append("BuildLauncher: 'gradleDaemonMaxIdleTime' is null");
            this.gradleDaemonMaxIdleTime = 0;
        } else this.gradleDaemonMaxIdleTime = launcher.getGradleDaemonMaxIdleTime();

        if (launcher.getStartup() != null) {
            startup = new Startup(launcher.getStartup(), errorBuilder);
        } else {
            errorBuilder.append("BuildLauncher: 'startup' is null");
            startup = null;
        }

        if (launcher.getProbe() != null) {
            probe = new Probe(launcher.getProbe(), errorBuilder);
        } else {
            errorBuilder.append("BuildLauncher: 'probe' is null");
            probe = null;
        }

        if (launcher.getBuild() != null) {
            build = new Build(launcher.getBuild(), errorBuilder);
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
        protected BiFunction<Managers, Dependency, List<String>> defaultArguments(@Nullable String[] args) {
            if (args != null) {
                final List<String> argsList = Arrays.asList(args);
                return (managers, dep) -> argsList;
            } else {
                return (managers, dep) -> Collections.emptyList();
            }
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
        protected BiFunction<Managers, Dependency, List<String>> defaultArguments(@Nullable String[] args) {
            if (args != null) {
                PluginLogger.warn("Custom main tasks arguments detected for Probe launcher, this is not recommended");
                final List<String> argsList = Arrays.asList(args);
                return (managers, dep) -> argsList;
            } else {
                return (managers, dep) -> {
                    File initScriptFolder = managers.getPropertyManager().getPluginConfig().getPersistentDir();
                    File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);
                    return Arrays.asList("--init-script", mainInit.getAbsolutePath());
                };
            }
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
        protected BiFunction<Managers, Dependency, List<String>> defaultArguments(@Nullable String[] args) {
            if (args != null) {
                PluginLogger.warn("Custom main tasks arguments detected for Build launcher, this is not recommended");
                final List<String> argsList = Arrays.asList(args);
                return (managers, dep) -> argsList;
            } else {
                return (managers, dep) -> {
                    File initScriptFolder = managers.getPropertyManager().getPluginConfig().getPersistentDir();
                    File mainInit = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_GRADLE);
                    return Arrays.asList("--init-script", mainInit.getAbsolutePath());
                };
            }
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
                            return Collections.singletonList("build");

                        case MavenLocal:
                            return Collections.singletonList(Constants.PublicationTaskName.apply(dep.getPersistentInfo().getProbeData().getPublicationData()));

                        case MavenProjectDependencyLocal:
                        case MavenProjectLocal:
                            PublicationData publicationData = dep.getPersistentInfo().getProbeData().getPublicationData();
                            return Collections.singletonList(Constants.FilePublicationTaskName.apply(publicationData));

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

            this.preTasksArguments = defaultArguments(config.getPreTasksArguments());
            this.preTasks = getTasks(config.getPreTasks());

            this.mainTasksArguments = defaultArguments(config.getMainTasksArguments());
            this.mainTasks = defaultMainTasks(config);

            this.postTasksArguments = defaultArguments(config.getPostTasksArguments());
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

        protected abstract BiFunction<Managers, Dependency, List<String>> defaultArguments(@Nullable String[] args);

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
