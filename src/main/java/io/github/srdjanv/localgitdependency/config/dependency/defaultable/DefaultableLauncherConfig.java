package io.github.srdjanv.localgitdependency.config.dependency.defaultable;

import org.gradle.api.provider.Property;

public interface DefaultableLauncherConfig {
    /**
     * The Java the installation path to be used by the gradle process
     * This can take a JavaLauncher, RegularFile, File, Path, String and Property, Provider of anny of these types
     */
    Property<Object> getExecutable();

    /**
     * For how long should the gradle daemon used for dependency building idle.
     * Use java's TimeUnit class for easy conversion
     *
     * @see java.util.concurrent.TimeUnit
     */
    Property<Integer> getGradleDaemonMaxIdleTime();

    /**
     * Setting this to true will forward logs to the standard output,
     * Default true
     */
    Property<Boolean> getForwardOutput();
}
