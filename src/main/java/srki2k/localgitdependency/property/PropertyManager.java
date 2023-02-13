package srki2k.localgitdependency.property;

import groovy.lang.Closure;
import org.gradle.api.GradleException;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.depenency.Dependency;

import java.lang.reflect.Field;

public class PropertyManager {
    private boolean createdEssentialDirectories;
    private boolean customGlobalProperty;
    private DefaultProperty globalProperty;

    {
        DefaultProperty.Builder builder = new DefaultProperty.Builder();
        builder.defaultConfiguration(Constants.JAVA_IMPLEMENTATION);
        builder.persistentFolder(Constants.defaultPersistentDir.apply(Constants.defaultDir.get()));
        builder.dir(Constants.defaultLibsDir.apply(Constants.defaultDir.get()));
        builder.dependencyType(Dependency.DependencyType.MavenLocal);
        builder.keepGitUpdated(true);
        builder.manualBuild(false);
        builder.gradleProbeCashing(true);

        globalProperty = new DefaultProperty(builder);
    }

    private void createEssentialDirectories() {
        if (createdEssentialDirectories) return;

        if (!globalProperty.persistentFolder.exists()) {
            globalProperty.persistentFolder.mkdirs();
        } else if (!globalProperty.persistentFolder.isDirectory()) {
            throw new GradleException(globalProperty.persistentFolder.getAbsolutePath() + " is not a directory, delete the file and refresh gradle");
        }

        if (!globalProperty.dir.exists()) {
            globalProperty.dir.mkdirs();
        } else if (!globalProperty.dir.isDirectory()) {
            throw new GradleException(globalProperty.dir.getAbsolutePath() + " is not a directory, delete the file and refresh gradle");
        }
        createdEssentialDirectories = true;
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
        createEssentialDirectories();
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
