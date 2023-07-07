package io.github.srdjanv.localgitdependency.config.dependency.defaultable;

public interface DefaultableLauncherBuilder {
    void setExecutable(Object path);

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @param gradleDaemonMaxIdleTime the amount of time in seconds
     * @see java.util.concurrent.TimeUnit
     */
    void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime);

    // TODO: 24/06/2023
    void forwardOutput(Boolean forwardOutput);
}
