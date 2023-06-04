package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.jetbrains.annotations.NotNull;

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

    public static <D> void mergeObjectsDefaultReference(D newObject, D referenceObject, Class<D> clazz) {
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

    public static <D> void mergeObjectsDefaultNewObject(D newObject, D referenceObject, Class<D> clazz) {
        Class<?> currentClazz = clazz;
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object referenceObjectField = field.get(referenceObject);

                    if (referenceObjectField != null) {
                        field.set(newObject, referenceObjectField);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
    }

    @NotNull
    public static List<String> validateDataDefault(Object object) {
        List<String> nulls = new ArrayList<>();
        validateDataDefaultInternal(object, defaultDataNullable(object.getClass()), nulls);

        return nulls;
    }

    private static void validateDataDefaultInternal(Object object, Boolean defaultDataNullable, List<String> nulls) {
        Class<?> currentClazz = object.getClass();
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    var obj = field.get(object);
                    if (defaultDataNullable == null) {
                        var fieldType = field.getType();
                        if (isClassAnnotatedWithNullableData(fieldType)
                                || isClassAnnotatedWithNonNullData(fieldType)) {
                            validateDataDefaultInternal(obj, defaultDataNullable(fieldType), nulls);
                        }
                    } else if (defaultDataNullable) {
                        if (field.isAnnotationPresent(NonNullData.class)) {
                            if (obj == null) {
                                nulls.add(String.format("Field %s is null", field.getName()));
                            }
                        }

                    } else {
                        if (obj == null) {
                            if (field.isAnnotationPresent(NullableData.class)) {
                                continue;
                            }
                            nulls.add(String.format("Field %s is null", field.getName()));
                        }

                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
    }

    public static boolean isClassAnnotatedWithNonNullData(Class<?> clazz) {
        return clazz.isAnnotationPresent(NonNullData.class);
    }

    public static boolean isClassAnnotatedWithNullableData(Class<?> clazz) {
        return clazz.isAnnotationPresent(NullableData.class);
    }

    public static Boolean defaultDataNullable(Class<?> clazz) {
        if (isClassAnnotatedWithNonNullData(clazz)) {
            return false;
        } else if (isClassAnnotatedWithNullableData(clazz)) {
            return true;
        } else {
            return null;
        }
    }
}
