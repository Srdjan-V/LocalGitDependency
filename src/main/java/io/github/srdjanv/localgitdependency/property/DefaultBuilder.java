package io.github.srdjanv.localgitdependency.property;

public interface DefaultBuilder {

    /**
     * If set to false the generated mainInitScript will new be updated of fixed if changes are detected
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
     * Cleanup removed dependencies
     *
     * @param automaticCleanup if it should cleanup removed dependencies
     */
    void automaticCleanup(Boolean automaticCleanup);
}
