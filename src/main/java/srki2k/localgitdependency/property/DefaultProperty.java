package srki2k.localgitdependency.property;

//Property's for global configuration
public class DefaultProperty extends CommonPropertyGetters {
    private final Boolean keepInitScriptUpdated;

    public DefaultProperty(Builder builder) {
        keepInitScriptUpdated = builder.keepInitScriptUpdated;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public DefaultProperty() {
        keepInitScriptUpdated = null;
    }

    public boolean isKeepInitScriptUpdated() {
        return keepInitScriptUpdated;
    }

    public static class Builder extends CommonPropertyBuilder {
        private Boolean keepInitScriptUpdated;

        public void keepInitScriptUpdated(Boolean keepInitScriptUpdated) {
            this.keepInitScriptUpdated = keepInitScriptUpdated;
        }
    }
}
