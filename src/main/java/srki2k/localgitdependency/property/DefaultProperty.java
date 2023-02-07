package srki2k.localgitdependency.property;

//Property's for global configuration
public class DefaultProperty extends CommonPropertyGetters {
    public DefaultProperty(Builder builder) {
        if (builder != null) {
            PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
        }
    }

    public static class Builder extends CommonProperty {
    }
}
