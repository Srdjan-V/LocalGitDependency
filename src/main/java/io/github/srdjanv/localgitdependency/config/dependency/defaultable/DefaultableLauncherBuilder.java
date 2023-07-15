package io.github.srdjanv.localgitdependency.config.dependency.defaultable;

public interface DefaultableLauncherBuilder {
    /**
     * The Java the installation path to be used by the gradle process
     * This can take a JavaLauncher, RegularFile, File, Path, String and Property, Provider of anny of these types
     * @param path the installation path
     */
    void setExecutable(Object path);

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @param gradleDaemonMaxIdleTime the amount of time in seconds
     * @see java.util.concurrent.TimeUnit
     */
    void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime);

    /**
     * Setting this to true will forward logs to the standard output,
     * Default true
     * @param forwardOutput setting forwarding
     */
    void forwardOutput(Boolean forwardOutput);
}
