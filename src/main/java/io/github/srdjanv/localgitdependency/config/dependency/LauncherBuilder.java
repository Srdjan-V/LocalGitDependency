package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface LauncherBuilder {
    void setExecutable(Object path);

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @param gradleDaemonMaxIdleTime the amount of time in seconds
     * @see java.util.concurrent.TimeUnit
     */
    void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime);

    void startup(@DelegatesTo(value = Launchers.Startup.class,
            strategy = Closure.DELEGATE_FIRST) Closure startup);

    void probe(@DelegatesTo(value = Launchers.Probe.class,
            strategy = Closure.DELEGATE_FIRST) Closure probe);

    void build(@DelegatesTo(value = Launchers.Build.class,
            strategy = Closure.DELEGATE_FIRST) Closure build);
}
