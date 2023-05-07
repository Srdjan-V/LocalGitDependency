package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.property.impl.CommonPropertyFields;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;
import io.github.srdjanv.localgitdependency.util.ClosureUtil;
import org.gradle.api.GradleException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

class PropertyManager extends ManagerBase implements IPropertyManager {
    private boolean customGlobalProperty;
    private GlobalProperty globalProperty;

    PropertyManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
        GlobalProperty.Builder builder = new GlobalProperty.Builder();
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
        builder.enableIdeSupport(false);
        builder.registerDependencyRepositoryToProject(true);
        builder.gradleDaemonMaxIdleTime((int) TimeUnit.MINUTES.toSeconds(2));

        globalProperty = new GlobalProperty(builder);
    }

    @Override
    public void globalProperty(@SuppressWarnings("rawtypes") Closure configureClosure) {
        if (configureClosure != null) {
            if (customGlobalProperty) {
                throw new GradleException("You can't change the globalProperty once they are set");
            }
            GlobalProperty.Builder defaultProperty = new GlobalProperty.Builder();
            ClosureUtil.delegate(configureClosure, defaultProperty);
            configureFilePaths(defaultProperty);
            GlobalProperty newGlobalProperty = new GlobalProperty(defaultProperty);
            customPathsCheck(newGlobalProperty);
            this.globalProperty = mergeGlobalProperty(globalProperty, newGlobalProperty);
            customGlobalProperty = true;
        }
    }

    @Override
    public GlobalProperty getGlobalProperty() {
        return globalProperty;
    }

    private void customPathsCheck(GlobalProperty globalProperty) {
        if (globalProperty.getAutomaticCleanup() != null) {
            return;
        }

        Optional<File> optional = streamEssentialDirectories(globalProperty).filter(Objects::nonNull).findAny();
        if (optional.isPresent()) {
            throw new GradleException("Custom global directory paths detected, automaticCleanup must explicitly be set to true or false");
        }
    }

    @Override
    public void createEssentialDirectories() {
        streamEssentialDirectories(globalProperty).forEach(Constants::checkExistsAndMkdirs);
    }

    private static Stream<File> streamEssentialDirectories(GlobalProperty property) {
        return Stream.of(property.getPersistentDir(), property.getGitDir(), property.getMavenDir());
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
                throw new RuntimeException(String.format("Unexpected error while reflecting %s class", CommonPropertyFields.class), e);
            }
        }
    }

    /**
     * Merges the supplied dependencyProperty with the globalProperty in this manager
     * If a field in dependencyProperty is null the field in globalProperty will be used
     */
    @Override
    public void applyDefaultProperty(DependencyProperty dependencyProperty) {
        Class<CommonPropertyFields> clazz = CommonPropertyFields.class;
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(dependencyProperty) == null) {
                    field.set(dependencyProperty, field.get(globalProperty));
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Unexpected error while reflecting %s class", clazz), e);
            }
        }
    }

    /**
     * Merges 2 properties GlobalProperties, if a field in newGlobalProperty is null the field in defaultGlobalPropertyValues will be used
     */
    private static GlobalProperty mergeGlobalProperty(GlobalProperty defaultGlobalPropertyValues, GlobalProperty newGlobalProperty) {
        GlobalProperty resolvedProperty = new GlobalProperty();
        Class<?>[] classes = new Class[2];
        classes[0] = CommonPropertyFields.class;
        classes[1] = GlobalProperty.class;

        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object newGlobalPropertyField = field.get(newGlobalProperty);
                    Object defaultGlobalPropertyField = field.get(defaultGlobalPropertyValues);

                    if (newGlobalPropertyField == null) {
                        field.set(resolvedProperty, defaultGlobalPropertyField);
                    } else {
                        field.set(resolvedProperty, newGlobalPropertyField);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", clazz), e);
                }
            }
        }

        return resolvedProperty;
    }
}
