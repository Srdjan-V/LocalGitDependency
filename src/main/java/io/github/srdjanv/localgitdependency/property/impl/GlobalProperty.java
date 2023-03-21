package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.property.GlobalBuilder;
import io.github.srdjanv.localgitdependency.property.PropertyManager;

/**
 * Property's used for global configuration
 */
public class GlobalProperty extends CommonPropertyGetters {
    private final Boolean keepMainInitScriptUpdated;
    private final Boolean generateDefaultGradleTasks;
    private final Boolean automaticCleanup;

    public GlobalProperty(Builder builder) {
        keepMainInitScriptUpdated = builder.keepMainInitScriptUpdated;
        generateDefaultGradleTasks = builder.generateDefaultGradleTasks;
        automaticCleanup = builder.automaticCleanup;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public GlobalProperty() {
        keepMainInitScriptUpdated = null;
        generateDefaultGradleTasks = null;
        automaticCleanup = null;
    }

    public boolean getKeepMainInitScriptUpdated() {
        return keepMainInitScriptUpdated;
    }

    public Boolean getGenerateDefaultGradleTasks() {
        return generateDefaultGradleTasks;
    }

    public Boolean getAutomaticCleanup() {
        return automaticCleanup;
    }

    public static class Builder extends CommonPropertyBuilder implements GlobalBuilder {
        private Boolean keepMainInitScriptUpdated;
        private Boolean generateDefaultGradleTasks;
        private Boolean automaticCleanup;

        @Override
        public void keepMainInitScriptUpdated(Boolean keepMainInitScriptUpdated) {
            this.keepMainInitScriptUpdated = keepMainInitScriptUpdated;
        }

        @Override
        public void generateDefaultGradleTasks(Boolean generateDefaultGradleTasks) {
            this.generateDefaultGradleTasks = generateDefaultGradleTasks;
        }

        @Override
        public void automaticCleanup(Boolean automaticCleanup) {
            this.automaticCleanup = automaticCleanup;
        }
    }
}
