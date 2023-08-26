package io.github.srdjanv.localgitdependency.config.plugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

public interface PluginConfig {

    DirectoryProperty getLibsDir();

    /**
     * If set to false the generated mainInitScript will never be updated of fixed if changes are detected
     *
     */
    Property<Boolean> getKeepInitScriptUpdated();

    /**
     * This will generate default tasks
     *
     */
    Property<Boolean> getGenerateGradleTasks();

    /**
     * Cleanup removed dependencies. It's enabled by default, but if you specify a custom global path you must explicitly enable it.
     * This is done because the cleanupManager will delete everything under those directories that doesn't mach with a registered dependency
     *
     */
    Property<Boolean> getAutomaticCleanup();

    Property<Boolean> getDisablePluginExecution();
}
