package io.github.srdjanv.localgitdependency.config.impl.dependency;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.config.dependency.LauncherBuilder;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Base;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Build;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Probe;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers.Startup;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.jetbrains.annotations.Nullable;

public final class Launchers {
    private Launchers() {
    }

    public static class Launcher {
        private final Closure startup;
        private final Closure probe;
        private final Closure build;

        public Launcher(Builder builder) {
            this.startup = builder.startup;
            this.probe = builder.probe;
            this.build = builder.build;
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

        public static class Builder implements LauncherBuilder {
            private Closure startup;
            private Closure probe;
            private Closure build;

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

    public static class BaseBuilder implements Base {
        Object executable;
        JavaLauncher launcher;
        Boolean explicit;
        String[] preTasks;
        String[] mainTasks;
        String[] postTasks;
        String[] setTaskTriggers;
        String[] addTaskTriggers;

/*
        @Override
        public void setExecutable(Object path) {
            executable = path;
        }

        @Override
        public void setJavaLauncher(JavaLauncher launcher) {
            this.launcher = launcher;
        }
*/

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

    public static class BaseLauncherConfig {
        private final Object executable;
        private final JavaLauncher launcher;
        private final Boolean explicit;
        private final String[] preTasks;
        private final String[] mainTasks;
        private final String[] postTasks;
        private final String[] setTaskTriggers;
        private final String[] addTaskTriggers;

        BaseLauncherConfig(BaseBuilder builder) {
            this.executable = builder.executable;
            this.launcher = builder.launcher;
            this.explicit = builder.explicit;
            this.preTasks = builder.preTasks;
            this.mainTasks = builder.mainTasks;
            this.postTasks = builder.postTasks;
            this.setTaskTriggers = builder.setTaskTriggers;
            this.addTaskTriggers = builder.addTaskTriggers;
        }

        @Nullable
        public Object getExecutable() {
            return executable;
        }

        @Nullable
        public JavaLauncher getLauncher() {
            return launcher;
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
}
