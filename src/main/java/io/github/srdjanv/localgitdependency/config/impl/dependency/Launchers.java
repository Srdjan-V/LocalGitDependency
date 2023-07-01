package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Base;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Build;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Probe;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Startup;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

public final class Launchers {
    private Launchers() {
    }

    @NonNullData
    public static class Launcher extends LauncherFields {
        private final StartupConfig startupConfig;
        private final ProbeConfig probeConfig;
        private final BuildConfig buildConfig;

        public Launcher(Builder builder) {
            ClassUtil.instantiateObjectWithBuilder(this, builder, LauncherFields.class);

            startupConfig = buildLauncher(
                    new StartupConfig.Builder(),
                    builder.startup,
                    StartupConfig::new
            );
            probeConfig = buildLauncher(
                    new ProbeConfig.Builder(),
                    builder.probe,
                    ProbeConfig::new
            );
            buildConfig = buildLauncher(
                    new BuildConfig.Builder(),
                    builder.build,
                    BuildConfig::new
            );
        }

        private static <B extends BaseBuilder, C extends BaseLauncherConfig> C buildLauncher(
                @NotNull B configBuilder,
                @Nullable Closure closure,
                Function<B, C> configFunction
        ) {
            if (ClosureUtil.delegateNullSafe(closure, configBuilder)) {
                return configFunction.apply(configBuilder);
            } else throw new NullPointerException("BuildLauncher: a launcher is null");
        }

        public Launcher(Builder builder, Launcher defaultable) {
            ClassUtil.mergeObjectsDefaultReference(this, defaultable, LauncherFields.class);
            ClassUtil.mergeObjectsDefaultNewObject(this, builder, LauncherFields.class);

            startupConfig = defaultableBuildLauncher(
                    new StartupConfig.Builder(),
                    builder.startup,
                    StartupConfig::new,
                    defaultable.getStartup(),
                    StartupConfig.class);

            probeConfig = defaultableBuildLauncher(
                    new ProbeConfig.Builder(),
                    builder.probe,
                    ProbeConfig::new,
                    defaultable.getProbe(),
                    ProbeConfig.class);

            buildConfig = defaultableBuildLauncher(
                    new BuildConfig.Builder(),
                    builder.build,
                    BuildConfig::new,
                    defaultable.getBuild(),
                    BuildConfig.class);
        }

        private static <B extends BaseBuilder, C extends BaseLauncherConfig> C defaultableBuildLauncher(
                @NotNull B configBuilder,
                @Nullable Closure closure,
                Function<B, C> configFunction,
                @Nullable C fallback,
                Class<C> fields
        ) {
            if (ClosureUtil.delegateNullSafe(closure, configBuilder)) {
                if (fallback != null) {
                    var conf = configFunction.apply(configBuilder);
                    ClassUtil.mergeObjectsDefaultReference(conf, fallback, fields);
                    return conf;
                }

                return configFunction.apply(configBuilder);
            } else if (fallback != null) {
                return fallback;
            } else throw new NullPointerException("BuildLauncher: a launcher is null");
        }

        @Nullable
        public File getExecutable() {
            return executable;
        }

        @Nullable
        public Integer getGradleDaemonMaxIdleTime() {
            return gradleDaemonMaxIdleTime;
        }

        @NotNull
        public StartupConfig getStartup() {
            return startupConfig;
        }

        @NotNull
        public ProbeConfig getProbe() {
            return probeConfig;
        }

        @NotNull
        public BuildConfig getBuild() {
            return buildConfig;
        }

        public static class Builder extends LauncherFields implements LauncherBuilder {
            private Closure startup;
            private Closure probe;
            private Closure build;
            private Boolean forwardOutput;

            @Override
            public void setExecutable(Object path) {
                this.executable = FileUtil.toFile(path, "setExecutable");
            }

            @Override
            public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
                this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
            }


            @Override
            public void forwardOutput(Boolean forwardOutput) {
                this.forwardOutput = forwardOutput;
            }

            @Override
            public void startup(Closure startup) {
                this.startup = startup;
            }

            @Override
            public void probe(Closure probe) {
                this.probe = probe;
            }

            @Override
            public void build(Closure build) {
                this.build = build;
            }
        }
    }

    public static abstract class LauncherFields {
        @NullableData
        protected File executable;
        protected Integer gradleDaemonMaxIdleTime;
    }

    public static class StartupConfig extends BaseLauncherConfig {
        public StartupConfig(Builder builder) {
            super(builder);
        }

        public static class Builder extends BaseBuilder implements Startup {
        }
    }

    public static class ProbeConfig extends BaseLauncherConfig {
        public ProbeConfig(Builder builder) {
            super(builder);
        }

        public static class Builder extends BaseBuilder implements Probe {
        }
    }

    public static class BuildConfig extends BaseLauncherConfig {
        public BuildConfig(Builder builder) {
            super(builder);
        }

        public static class Builder extends BaseBuilder implements Build {
        }
    }

    public static class BaseLauncherConfig extends BaseLauncherFields {
        public BaseLauncherConfig(BaseBuilder builder) {
            ClassUtil.instantiateObjectWithBuilder(this, builder, BaseLauncherFields.class);
        }

        @Nullable
        public String[] getPreTasksArguments() {
            return preTasksArguments;
        }

        @Nullable
        public String[] getMainTasksArguments() {
            return mainTasksArguments;
        }

        @Nullable
        public String[] getPostTasksArguments() {
            return postTasksArguments;
        }

        @Nullable
        public Boolean getExplicit() {
            return explicit;
        }

        @Nullable
        public String[] getPreTasks() {
            return preTasks;
        }

        @Nullable
        public String[] getMainTasks() {
            return mainTasks;
        }

        @Nullable
        public String[] getPostTasks() {
            return postTasks;
        }

        @Nullable
        public String[] getSetTaskTriggers() {
            return setTaskTriggers;
        }

        @Nullable
        public String[] getAddTaskTriggers() {
            return addTaskTriggers;
        }

        @Nullable
        public Boolean getForwardOutput() {
            return forwardOutput;
        }
    }

    public static class BaseBuilder extends BaseLauncherFields implements Base {
        @Override
        public void explicit(Boolean explicit) {
            this.explicit = explicit;
        }

        @Override
        public void preTasksWithArguments(String... args) {
            preTasksArguments = args;
        }

        @Override
        public void preTasks(String... tasks) {
            preTasks = tasks;
        }

        @Override
        public void mainTasksWithArguments(String... args) {
            mainTasksArguments = args;
        }

        @Override
        public void mainTasks(String... tasks) {
            preTasks = tasks;
        }

        @Override
        public void postTasksWithArguments(String... args) {
            postTasksArguments = args;
        }

        @Override
        public void postTasks(String... tasks) {
            preTasks = tasks;
        }

        @Override
        public void setTaskTriggers(String... files) {
            setTaskTriggers = files;
        }

        @Override
        public void addTaskTriggers(String... files) {
            addTaskTriggers = files;
        }

        @Override
        public void forwardOutput(Boolean forwardOutput) {
            this.forwardOutput = forwardOutput;
        }
    }

    public static abstract class BaseLauncherFields {
        protected Boolean explicit;
        protected String[] preTasksArguments;
        protected String[] preTasks;
        protected String[] mainTasksArguments;
        protected String[] mainTasks;
        protected String[] postTasksArguments;
        protected String[] postTasks;
        protected String[] setTaskTriggers;
        protected String[] addTaskTriggers;
        protected Boolean forwardOutput;
    }
}
