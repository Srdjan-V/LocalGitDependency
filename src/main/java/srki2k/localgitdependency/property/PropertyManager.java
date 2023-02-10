package srki2k.localgitdependency.property;

import groovy.lang.Closure;
import org.gradle.api.GradleException;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.depenency.Dependency;

import java.lang.reflect.Field;

public class PropertyManager {
    private boolean customGlobalProperty;
    private DefaultProperty globalProperty;

    {
        DefaultProperty.Builder builder = new DefaultProperty.Builder();
        builder.defaultConfiguration(Constants.JAVA_IMPLEMENTATION);
        builder.persistentFolder(Constants.defaultPersistentDir.apply(Constants.defaultLibDirs.get()));
        builder.dir(Constants.defaultLibDirs.get());
        builder.dependencyType(Dependency.DependencyType.MavenLocal);
        builder.keepGitUpdated(true);
        builder.manualBuild(false);

        globalProperty = new DefaultProperty(builder);
    }

    public void globalProperty(Closure<?> configureClosure) {
        if (configureClosure != null) {
            if (customGlobalProperty) {
                throw new GradleException("you cant change the globalProperty once they are set");
            }
            DefaultProperty.Builder defaultProperty = new DefaultProperty.Builder();
            configureClosure.setDelegate(defaultProperty);
            configureClosure.call();
            this.globalProperty = resolveProperty(new DefaultProperty(defaultProperty));
            customGlobalProperty = true;
        }
    }

    public DefaultProperty getGlobalProperty() {
        return globalProperty;
    }

    //applies missing globalProperty from the defaultGlobalProperty
    private DefaultProperty resolveProperty(DefaultProperty newGlobalProperty) {
        DefaultProperty resolvedProperty = new DefaultProperty(null);
        for (Field field : CommonPropertyFields.class.getDeclaredFields()) {
            try {
                Object globalPropertyField = field.get(newGlobalProperty);
                Object defaultGlobalPropertyField = field.get(globalProperty);

                if (globalPropertyField == null) {
                    field.set(resolvedProperty, defaultGlobalPropertyField);
                } else {
                    field.set(resolvedProperty, globalPropertyField);
                }

            } catch (Exception e) {
                throw new GradleException("Unexpected error while reflecting CommonProperty class", e);
            }
        }

        return resolvedProperty;
    }

    //applies missing dependencyProperty from the globalProperty
    public void applyDefaultProperty(Property dependencyProperty) {
        for (Field field : CommonPropertyFields.class.getDeclaredFields()) {
            try {
                if (field.get(dependencyProperty) == null) {
                    field.set(dependencyProperty, field.get(globalProperty));
                }
            } catch (Exception e) {
                throw new GradleException("Unexpected error while reflecting CommonProperty class", e);
            }
        }
    }

    public static void instantiateCommonPropertyFieldsInstance(CommonPropertyFields object, CommonPropertyFields builder) {
        for (Field field : CommonPropertyFields.class.getDeclaredFields()) {
            try {
                field.set(object, field.get(builder));
            } catch (Exception e) {
                throw new GradleException("Unexpected error while reflecting CommonPropertyFields class", e);
            }
        }
    }

}
