package io.github.srdjanv.localgitdependency.config.plugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

public interface PluginConfig {

    DirectoryProperty libsDir();

    /**
     * If set to false the generated mainInitScript will never be updated of fixed if changes are detected
     *
     * @param keepInitScriptUpdated If it should stay updated
     */
    Property<Boolean> keepInitScriptUpdated();

    /**
     * This will generate default tasks
     *
     * @param generateGradleTasks if it should create custom tasks
     */
    Property<Boolean> generateGradleTasks();

    /**
     * Cleanup removed dependencies. It's enabled by default, but if you specify a custom global path you must explicitly enable it.
     * This is done because the cleanupManager will delete everything under those directories that doesn't mach with a registered dependency
     *
     * @param automaticCleanup if it should cleanup removed dependencies
     */
    Property<Boolean> automaticCleanup();
}
