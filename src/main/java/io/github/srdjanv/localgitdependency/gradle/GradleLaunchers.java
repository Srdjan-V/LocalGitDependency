package io.github.srdjanv.localgitdependency.gradle;

import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BuildConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.Launcher;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.ProbeConfig;
import io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.StartupConfig;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.github.srdjanv.localgitdependency.config.impl.dependency.Launchers.BaseLauncherConfig;

public final class GradleLaunchers {
    public static GradleLaunchers build(DependencyConfig dependencyConfig) {
        Launcher.Builder builder = new Launcher.Builder();
        ClosureUtil.delegateNullSafe(dependencyConfig.getLauncher(), builder);
        return new GradleLaunchers(new Launcher(builder));
    }

    private final Startup startup;
    private final Probe probe;
    private final Build build;

    private GradleLaunchers(Launcher launcher) {
        StartupConfig.Builder startupConfigBuilder = new StartupConfig.Builder();
        ClosureUtil.delegateNullSafe(launcher.getStartup(), startupConfigBuilder);
        startup = new Startup(new StartupConfig(startupConfigBuilder));

        ProbeConfig.Builder probeConfigBuilder = new ProbeConfig.Builder();
        ClosureUtil.delegateNullSafe(launcher.getProbe(), probeConfigBuilder);
        probe = new Probe(new ProbeConfig(probeConfigBuilder));

        BuildConfig.Builder buildConfigBuilder = new BuildConfig.Builder();
        ClosureUtil.delegateNullSafe(launcher.getBuild(), buildConfigBuilder);
        build = new Build(new BuildConfig(buildConfigBuilder));
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

    public class Startup extends Base {
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
