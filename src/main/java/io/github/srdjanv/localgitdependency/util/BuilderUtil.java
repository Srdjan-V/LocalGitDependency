package io.github.srdjanv.localgitdependency.util;

import org.gradle.api.GradleException;

import java.lang.reflect.Field;

public class BuilderUtil {
    public static <D> void instantiateObjectWithBuilder(D object, D builder, Class<D> fieldsClazz) {
        for (Field field : fieldsClazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(object, field.get(builder));
            } catch (Exception e) {
                throw new GradleException(String.format("Unexpected error while reflecting %s class", fieldsClazz), e);
            }
        }
    }

}
