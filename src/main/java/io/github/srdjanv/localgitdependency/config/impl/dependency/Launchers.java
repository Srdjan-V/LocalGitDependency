package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Base;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Build;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Probe;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Startup;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class Launchers {
    private Launchers() {
    }

    public static class Launcher extends LauncherFields {
        public Launcher(Builder builder) {
            ClassUtil.instantiateObjectWithBuilder(this, builder, LauncherFields.class);
        }

        @Nullable
        public File getExecutable() {
            return executable;
        }

        @Nullable
        public Integer getGradleDaemonMaxIdleTime() {
            return gradleDaemonMaxIdleTime;
        }

        @Nullable
        public Closure getStartup() {
            return startup;
        }

        @Nullable
        public Closure getProbe() {
            return probe;
        }

        @Nullable
        public Closure getBuild() {
            return build;
        }

        public static class Builder extends LauncherFields implements LauncherBuilder {

            @Override
            public void setExecutable(Object path) {
                this.executable = FileUtil.toFile(path, "setExecutable");
            }

            @Override
            public void setExecutable(JavaLauncher javaLauncher) {
                this.executable = javaLauncher.getExecutablePath().getAsFile();
            }

            @Override
            public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
                this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
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
        protected Closure startup;
        protected Closure probe;
        protected Closure build;
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

        BaseLauncherConfig(BaseBuilder builder) {
            ClassUtil.instantiateObjectWithBuilder(this, builder, BaseLauncherFields.class);
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
    }

    public static class BaseBuilder extends BaseLauncherFields implements Base {
        @Override
        public void explicit(Boolean explicit) {
            this.explicit = explicit;
        }

        @Override
        public void preTasks(String... tasks) {
            preTasks = tasks;
        }

        @Override
        public void mainTasks(String... tasks) {
            preTasks = tasks;
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
    }

    public static class BaseLauncherFields {
        protected Boolean explicit;
        protected String[] preTasks;
        protected String[] mainTasks;
        protected String[] postTasks;
        protected String[] setTaskTriggers;
        protected String[] addTaskTriggers;
    }
}
