package com.srdjanv.localgitdependency.property;

import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
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
        builder.tryGeneratingSourceJar(true);
        builder.tryGeneratingJavaDocJar(false);

        globalProperty = new DefaultProperty(builder);
    }

    public void createEssentialDirectories() {
        Constants.checkExistsAndMkdirs(globalProperty.persistentFolder);
        Constants.checkExistsAndMkdirs(globalProperty.gitDir);
        Constants.checkExistsAndMkdirs(globalProperty.mavenFolder);
    }

    public void globalProperty(Closure<DefaultProperty.Builder> configureClosure) { // TODO: 04/03/2023 automatically configure file paths 
        if (configureClosure != null) {
            if (customGlobalProperty) {
                throw new GradleException("you cant change the globalProperty once they are set");
            }
            DefaultProperty.Builder defaultProperty = new DefaultProperty.Builder();
            configureClosure.setDelegate(defaultProperty);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
            this.globalProperty = resolveGlobalProperty(new DefaultProperty(defaultProperty));
            customGlobalProperty = true;
        }
    }

    public DefaultProperty getGlobalProperty() {
        return globalProperty;
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
