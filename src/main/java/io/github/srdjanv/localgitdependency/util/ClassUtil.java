package io.github.srdjanv.localgitdependency.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class ClassUtil {
    private ClassUtil() {
    }

    public static <D> void instantiateObjectWithBuilder(D object, D builder, final Class<D> fieldsClazz) {
        Class<?> currentClazz = fieldsClazz;
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    field.set(object, field.get(builder));
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
    }

    public static <D> void mergeObjects(D newObject, D referenceObject, Class<D> clazz) {
        Class<?> currentClazz = clazz;
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object newObjectField = field.get(newObject);
                    Object referenceObjectField = field.get(referenceObject);

                    if (newObjectField == null) {
                        field.set(newObject, referenceObjectField);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
    }

    @Nullable
    public static <D> List<String> validateNotNull(D object, Class<D> clazz) {
        List<String> nulls = null;
        Class<?> currentClazz = clazz;
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    var obj = field.get(object);
                    if (obj == null) {
                        if (nulls == null) {
                            nulls = new ArrayList<>();
                        }
                        nulls.add(String.format("Field %s is null", field.getName()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
        return nulls;
    }

}
