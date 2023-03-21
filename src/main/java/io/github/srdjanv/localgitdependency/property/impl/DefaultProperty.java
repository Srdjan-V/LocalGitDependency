package io.github.srdjanv.localgitdependency.property.impl;

import io.github.srdjanv.localgitdependency.property.DefaultBuilder;
import io.github.srdjanv.localgitdependency.property.PropertyManager;

/**
 * Property's used for global configuration
 */
public class DefaultProperty extends CommonPropertyGetters {
    private final Boolean keepMainInitScriptUpdated;
    private final Boolean generateDefaultGradleTasks;
    private final Boolean automaticCleanup;

    public DefaultProperty(Builder builder) {
        keepMainInitScriptUpdated = builder.keepMainInitScriptUpdated;
        generateDefaultGradleTasks = builder.generateDefaultGradleTasks;
        automaticCleanup = builder.automaticCleanup;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public DefaultProperty() {
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

    public static class Builder extends CommonPropertyBuilder implements DefaultBuilder {
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
