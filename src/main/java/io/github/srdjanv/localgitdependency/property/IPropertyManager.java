package io.github.srdjanv.localgitdependency.property;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.project.Manager;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.project.TaskDescription;
import io.github.srdjanv.localgitdependency.property.impl.CommonPropertyFields;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import io.github.srdjanv.localgitdependency.property.impl.GlobalProperty;
import org.gradle.api.GradleException;

import java.lang.reflect.Field;

public interface IPropertyManager extends Manager {
    static IPropertyManager createInstance(Managers managers) {
        return new PropertyManager(managers);
    }

    void globalProperty(Closure<GlobalBuilder> configureClosure);
    GlobalProperty getGlobalProperty();
    @TaskDescription("create essential directories")
    void createEssentialDirectories();
    void applyDefaultProperty(DependencyProperty dependencyDependencyProperty);

    static void instantiateCommonPropertyFieldsInstance(CommonPropertyFields object, CommonPropertyFields builder) {
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
