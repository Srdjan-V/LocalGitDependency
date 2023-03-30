package io.github.srdjanv.localgitdependency.property;

public interface GlobalBuilder extends CommonBuilder {

    /**
     * If set to false the generated mainInitScript will never be updated of fixed if changes are detected
     *
     * @param keepMainInitScriptUpdated If it should stay updated
     */
    void keepMainInitScriptUpdated(Boolean keepMainInitScriptUpdated);

    /**
     * This will generate default tasks
     *
     * @param generateDefaultGradleTasks if it should create custom tasks
     */
    void generateDefaultGradleTasks(Boolean generateDefaultGradleTasks);

    /**
     * Cleanup removed dependencies. It's enabled by default, but if you specify a custom global path you must explicitly enable it.
     * This is done because the cleanupManager will delete everything under those directories that doesn't mach with a registered dependency
     *
     * @param automaticCleanup if it should cleanup removed dependencies
     */
    void automaticCleanup(Boolean automaticCleanup);
}
