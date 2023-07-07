package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Base;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Build;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Probe;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Startup;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableLauncherConfig;
import io.github.srdjanv.localgitdependency.config.impl.defaultable.DefaultableLauncherConfigFields;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
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

        public Launcher(DefaultableLauncherConfig defaultable) {
            ClassUtil.instantiateObjectWithBuilder(this, defaultable, DefaultableLauncherConfigFields.class);

            // TODO: 07/07/2023 remove unnecessary builder
            startupConfig = new StartupConfig(new StartupConfig.Builder());
            probeConfig = new ProbeConfig(new ProbeConfig.Builder());
            buildConfig = new BuildConfig(new BuildConfig.Builder());
        }

        public Launcher(Builder builder, DefaultableLauncherConfig defaultable) {
            ClassUtil.mergeObjectsDefaultReference(this, defaultable, DefaultableLauncherConfigFields.class);
            ClassUtil.mergeObjectsDefaultNewObject(this, builder, LauncherFields.class);

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

        private <B extends BaseBuilder, C extends BaseLauncherConfig> C buildLauncher(
                @NotNull B configBuilder,
                @Nullable Closure closure,
                Function<B, C> configFunction
        ) {
            configBuilder.forwardOutput(forwardOutput);
            ClosureUtil.delegateNullSafe(closure, configBuilder);
            return configFunction.apply(configBuilder);
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

    public static abstract class LauncherFields extends DefaultableLauncherConfigFields {
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
