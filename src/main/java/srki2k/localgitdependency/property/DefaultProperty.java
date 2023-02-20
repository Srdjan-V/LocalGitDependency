package srki2k.localgitdependency.property;

//Property's for global configuration
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

        public void keepMainInitScriptUpdated(Boolean keepMainInitScriptUpdated) {
            this.keepMainInitScriptUpdated = keepMainInitScriptUpdated;
        }
    }
}
