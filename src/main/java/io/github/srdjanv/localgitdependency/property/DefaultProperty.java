package io.github.srdjanv.localgitdependency.property;

/**
 * Property's used for global configuration
 */
public class DefaultProperty extends CommonPropertyGetters {
    private final Boolean keepMainInitScriptUpdated;
    private final Boolean generateDefaultGradleTasks;

    public DefaultProperty(Builder builder) {
        keepMainInitScriptUpdated = builder.keepMainInitScriptUpdated;
        generateDefaultGradleTasks = builder.generateDefaultGradleTasks;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    DefaultProperty() {
        keepMainInitScriptUpdated = null;
        generateDefaultGradleTasks = null;
    }

    public boolean getKeepMainInitScriptUpdated() {
        return keepMainInitScriptUpdated;
    }

    public Boolean getGenerateDefaultGradleTasks() {
        return generateDefaultGradleTasks;
    }

    public static class Builder extends CommonPropertyBuilder {
        private Boolean keepMainInitScriptUpdated;
        private Boolean generateDefaultGradleTasks;

        /**
         * If set to false the generated mainInitScript will new be updated of fixed if changes are detected
         *
         * @param keepMainInitScriptUpdated If it should stay updated
         */
        public void keepMainInitScriptUpdated(Boolean keepMainInitScriptUpdated) {
            this.keepMainInitScriptUpdated = keepMainInitScriptUpdated;
        }

        /**
         * This will generate default tasks
         *
         * @param generateDefaultGradleTasks if it should create custom tasks
         */
        public void generateDefaultGradleTasks(Boolean generateDefaultGradleTasks) {
            this.generateDefaultGradleTasks = generateDefaultGradleTasks;
        }

    }
}
