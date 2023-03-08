package io.github.srdjanv.localgitdependency.property;

/**
 * Property's used for global configuration
 */
public class DefaultProperty extends CommonPropertyGetters {
    private final Boolean keepMainInitScriptUpdated;

    public DefaultProperty(Builder builder) {
        keepMainInitScriptUpdated = builder.keepMainInitScriptUpdated;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public DefaultProperty() {
        keepMainInitScriptUpdated = null;
    }

    public boolean getKeepMainInitScriptUpdated() {
        return keepMainInitScriptUpdated;
    }

    public static class Builder extends CommonPropertyBuilder {
        private Boolean keepMainInitScriptUpdated;

        /**
         * If set to false the generated mainInitScript will new be updated of fixed if changes are detected
         *
         * @param keepMainInitScriptUpdated If it should stay updated
         */
        public void keepMainInitScriptUpdated(Boolean keepMainInitScriptUpdated) {
            this.keepMainInitScriptUpdated = keepMainInitScriptUpdated;
        }
    }
}
