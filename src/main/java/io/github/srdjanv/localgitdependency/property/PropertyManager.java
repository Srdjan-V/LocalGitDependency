package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.impl.CommonPropertyFields;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;
import org.gradle.api.GradleException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

class PropertyManager extends ManagerBase implements IPropertyManager {
    private boolean customGlobalProperty;
    private GlobalProperty globalProperty;

    PropertyManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        GlobalProperty.Builder builder = new GlobalProperty.Builder();
        builder.configuration(Constants.JAVA_IMPLEMENTATION);
        File defaultDir = Constants.defaultDir.apply(getProject());
        builder.persistentDir(Constants.defaultPersistentDir.apply(defaultDir));
        builder.gitDir(Constants.defaultLibsDir.apply(defaultDir));
        builder.mavenDir(Constants.defaultMavenFolder.apply(defaultDir));
        builder.dependencyType(Dependency.Type.JarFlatDir);
        builder.automaticCleanup(true);
        builder.keepGitUpdated(true);
        builder.keepMainInitScriptUpdated(true);
        builder.generateDefaultGradleTasks(true);
        builder.generateGradleTasks(true);
        builder.keepDependencyInitScriptUpdated(true);
        builder.tryGeneratingSourceJar(false);
        builder.tryGeneratingJavaDocJar(false);
        builder.enableIdeSupport(true);
        builder.registerDependencyToProject(true);
        builder.registerDependencyRepositoryToProject(true);
        builder.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));

        globalProperty = new GlobalProperty(builder);
    }

    @Override
    public void globalProperty(Closure<GlobalProperty.Builder> configureClosure) {
        if (configureClosure != null) {
            if (customGlobalProperty) {
                throw new GradleException("You can't change the globalProperty once they are set");
            }
            GlobalProperty.Builder defaultProperty = new GlobalProperty.Builder();
            configureClosure.setDelegate(defaultProperty);
            configureClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
            configureClosure.call();
            configureFilePaths(defaultProperty);
            this.globalProperty = resolveGlobalProperty(new GlobalProperty(defaultProperty));
            customGlobalProperty = true;
        }
    }

    @Override
    public GlobalProperty getGlobalProperty() {
        return globalProperty;
    }

    @Override
    public void createEssentialDirectories() {
        Constants.checkExistsAndMkdirs(globalProperty.getPersistentDir());
        Constants.checkExistsAndMkdirs(globalProperty.getGitDir());
        Constants.checkExistsAndMkdirs(globalProperty.getMavenDir());
    }

    private void configureFilePaths(GlobalProperty.Builder defaultProperty) {
        File defaultDir = Constants.defaultDir.apply(getProject());
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
    private GlobalProperty resolveGlobalProperty(GlobalProperty newGlobalProperty) {
        GlobalProperty resolvedProperty = new GlobalProperty();
        Class<?>[] classes = new Class[2];
        classes[0] = CommonPropertyFields.class;
        classes[1] = GlobalProperty.class;

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
    @Override
    public void applyDefaultProperty(DependencyProperty dependencyDependencyProperty) {
        Class<CommonPropertyFields> clazz = CommonPropertyFields.class;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(dependencyDependencyProperty) == null) {
                    field.set(dependencyDependencyProperty, field.get(globalProperty));
                }
            } catch (Exception e) {
                throw new GradleException(String.format("Unexpected error while reflecting %s class", clazz), e);
            }
        }
    }

}
