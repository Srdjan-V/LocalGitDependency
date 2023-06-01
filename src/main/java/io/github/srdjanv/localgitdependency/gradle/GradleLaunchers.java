package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BuildConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.Launcher;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.ProbeConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.StartupConfig;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BaseLauncherConfig;

public final class GradleLaunchers {
    public static GradleLaunchers build(DependencyConfig dependencyConfig, ErrorUtil errorBuilder) {
        Launcher.Builder builder = new Launcher.Builder();
        if (ClosureUtil.delegateNullSafe(dependencyConfig.getLauncher(), builder)) {
            return new GradleLaunchers(new Launcher(builder), errorBuilder);
        }
        errorBuilder.append("DependencyConfig: 'buildLauncher' is null");
        return null;
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

        StartupConfig.Builder startupConfigBuilder = new StartupConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getStartup(), startupConfigBuilder)) {
            startup = new Startup(new StartupConfig(startupConfigBuilder));
        } else {
            errorBuilder.append("BuildLauncher: 'startup' is null");
            startup = null;
        }

        ProbeConfig.Builder probeConfigBuilder = new ProbeConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getProbe(), probeConfigBuilder)) {
            probe = new Probe(new ProbeConfig(probeConfigBuilder));
        } else {
            errorBuilder.append("BuildLauncher: 'probe' is null");
            probe = null;
        }

        BuildConfig.Builder buildConfigBuilder = new BuildConfig.Builder();
        if (ClosureUtil.delegateNullSafe(launcher.getBuild(), buildConfigBuilder)) {
            build = new Build(new BuildConfig(buildConfigBuilder));
        } else {
            errorBuilder.append("BuildLauncher: 'build' is null");
            build = null;
        }
    }

    public File getExecutable() {
        return executable;
    }
    public Integer getGradleDaemonMaxIdleTime() {
        return gradleDaemonMaxIdleTime;
    }

    public Startup getStartup() {
        return startup;
    }

    public Probe getProbe() {
        return probe;
    }

    public Build getBuild() {
        return build;
    }

    public static class Startup extends Base {
        private Startup(StartupConfig config) {
            super(config);
        }

        @Override
        protected List<String> defaultTriggers() {
            return new ArrayList<>();
        }
    }

    public static class Probe extends Base {
        private Probe(ProbeConfig config) {
            super(config);
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
        private Build(BuildConfig config) {
            super(config);
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

    private static abstract class Base {

        private final boolean explicit;
        private final List<String> preTasks;
        private final List<String> mainTasks;
        private final List<String> postTasks;
        private final List<String> taskTriggers;

        public Base(BaseLauncherConfig config) {
            this.explicit = config.getExplicit() != null && config.getExplicit();
            this.preTasks = getTasks(config.getPreTasks());
            this.mainTasks = getTasks(config.getMainTasks());
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
        }

        private List<String> getTasks(String[] tasks) {
            return tasks == null ? Collections.emptyList() : Arrays.asList(tasks);
        }

        protected abstract List<String> defaultTriggers();

        public boolean isExplicit() {
            return explicit;
        }

        @NotNull
        public List<String> getPreTasks() {
            return preTasks;
        }

        @NotNull
        public List<String> getMainTasks() {
            return mainTasks;
        }

        @NotNull
        public List<String> getPostTasks() {
            return postTasks;
        }

        @NotNull
        public List<String> getTaskTriggers() {
            return taskTriggers;
        }
    }

}
