package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import groovy.lang.Closure;
import org.gradle.api.GradleException;

import java.io.File;
import java.lang.reflect.Field;

public class PropertyManager {
    private boolean customGlobalProperty;
    private DefaultProperty globalProperty;

    {
        DefaultProperty.Builder builder = new DefaultProperty.Builder();
        builder.configuration(Constants.JAVA_IMPLEMENTATION);
        File defaultDir = Constants.defaultDir.get();
        builder.persistentFolder(Constants.defaultPersistentDir.apply(defaultDir));
        builder.gitDir(Constants.defaultLibsDir.apply(defaultDir));
        builder.mavenFolder(Constants.defaultMavenFolder.apply(defaultDir));
        builder.dependencyType(Dependency.Type.JarFlatDir);
        builder.keepGitUpdated(true);
        builder.keepMainInitScriptUpdated(true);
        builder.keepDependencyInitScriptUpdated(true);
        builder.tryGeneratingSourceJar(false);
        builder.tryGeneratingJavaDocJar(false);

        globalProperty = new DefaultProperty(builder);
    }

    public void globalProperty(Closure<DefaultProperty.Builder> configureClosure) {
        if (configureClosure != null) {
            if (customGlobalProperty) {
                throw new GradleException("You can't change the globalProperty once they are set");
            }
            DefaultProperty.Builder defaultProperty = new DefaultProperty.Builder();
            configureClosure.setDelegate(defaultProperty);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
            configureFilePaths(defaultProperty);
            this.globalProperty = resolveGlobalProperty(new DefaultProperty(defaultProperty));
            customGlobalProperty = true;
        }
    }

    public DefaultProperty getGlobalProperty() {
        return globalProperty;
    }

    public void createEssentialDirectories() {
        Constants.checkExistsAndMkdirs(globalProperty.persistentFolder);
        Constants.checkExistsAndMkdirs(globalProperty.gitDir);
        Constants.checkExistsAndMkdirs(globalProperty.mavenFolder);
    }

    private void configureFilePaths(DefaultProperty.Builder defaultProperty) {
        File defaultDir = Constants.defaultDir.get();
        for (Field field : CommonPropertyFields.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.getType() == File.class) {
                    File file = (File) field.get(defaultProperty);

                    if (file == null || file.isAbsolute()) continue;
                    field.set(defaultProperty, new File(defaultDir, String.valueOf(file)).toPath().normalize().toFile());
                }
            } catch (Exception e) {
                throw new GradleException(String.format("Unexpected error while reflecting %s class", CommonPropertyFields.class), e);
            }
        }
    }

    //applies missing globalProperty from the defaultGlobalProperty
    private DefaultProperty resolveGlobalProperty(DefaultProperty newGlobalProperty) {
        DefaultProperty resolvedProperty = new DefaultProperty();
        Class<?>[] classes = new Class[2];
        classes[0] = CommonPropertyFields.class;
        classes[1] = DefaultProperty.class;

        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object globalPropertyField = field.get(newGlobalProperty);
                    Object defaultGlobalPropertyField = field.get(globalProperty);

                    if (globalPropertyField == null) {
                        field.set(resolvedProperty, defaultGlobalPropertyField);
                    } else {
                        field.set(resolvedProperty, globalPropertyField);
                    }

                } catch (Exception e) {
                    throw new GradleException(String.format("Unexpected error while reflecting %s class", clazz), e);
                }
            }
        }

        return resolvedProperty;
    }

    //applies missing dependencyProperty from the globalProperty
    public void applyDefaultProperty(Property dependencyProperty) {
        Class<CommonPropertyFields> clazz = CommonPropertyFields.class;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(dependencyProperty) == null) {
                    field.set(dependencyProperty, field.get(globalProperty));
                }
            } catch (Exception e) {
                throw new GradleException(String.format("Unexpected error while reflecting %s class", clazz), e);
            }
        }
    }

    public static void instantiateCommonPropertyFieldsInstance(CommonPropertyFields object, CommonPropertyFields builder) {
        Class<CommonPropertyFields> clazz = CommonPropertyFields.class;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(object, field.get(builder));
            } catch (Exception e) {
                throw new GradleException(String.format("Unexpected error while reflecting %s class", clazz), e);
            }
        }
    }

}
