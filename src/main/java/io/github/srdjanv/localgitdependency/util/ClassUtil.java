package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ClassUtil {
    private ClassUtil() {
    }

    public static <D> void instantiateObjectWithBuilder(D object, D builder, final Class<D> fieldsClazz) {
        iterateFields(fieldsClazz, field -> {
            field.set(object, field.get(builder));
        });
    }

    public static <D> void mergeObjectsDefaultReference(D newObject, D referenceObject, Class<D> clazz) {
        iterateFields(clazz, field -> {
            Object newObjectField = field.get(newObject);
            Object referenceObjectField = field.get(referenceObject);

            if (newObjectField == null) {
                field.set(newObject, referenceObjectField);
            }
        });
    }

    public static <D> void mergeObjectsDefaultNewObject(D newObject, D referenceObject, Class<D> clazz) {
        iterateFields(clazz, field -> {
            Object referenceObjectField = field.get(referenceObject);

            if (referenceObjectField != null) {
                field.set(newObject, referenceObjectField);
            }
        });
    }

    //Used for probeData praising
    public static boolean validDataForClass(Class<?> clazz, Object data) {
        if (data == null) {
            return false;
        }

        if (!isClassAnnotatedWithNonNullData(clazz)) {
            return true;
        }

        try {
            Class<?> clazzIterator = clazz;
            do {
                for (Field declaredField : clazzIterator.getDeclaredFields()) {
                    declaredField.setAccessible(true);

                    //simple data for class like string
                    if (declaredField.get(data) == null) {
                        return false;
                    }

                    //inner objects that are annotated NonNullData
                    if (!validDataForClass(declaredField.getType(), declaredField.get(data))) {
                        return false;
                    }

                    //inner List objects with a generic type that is annotated with NonNullData
                    if (declaredField.getType() == List.class) {
                        Type genericType = declaredField.getGenericType();
                        if (genericType instanceof ParameterizedType parameterizedType) {
                            Type type = parameterizedType.getActualTypeArguments()[0];
                            if (type instanceof Class<?> listClazz) {
                                List<?> list = (List<?>) declaredField.get(data);

                                for (Object o : list) {
                                    if (!validDataForClass(listClazz, o)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                }
                clazzIterator = clazzIterator.getSuperclass();
            } while (clazzIterator != Object.class);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    //Used for validating pluginConfig and defaultableConfig, but targeting a specific class
    @NotNull
    public static <T> List<String> validateDataDefault(T object, Class<T> clazz) {
        List<String> nulls = new ArrayList<>();
        validateDataDefaultInternal(clazz, object, defaultDataNullable(clazz), nulls);

        return nulls;
    }

    //Used for validating pluginConfig and defaultableConfig
    @NotNull
    public static List<String> validateDataDefault(Object object) {
        List<String> nulls = new ArrayList<>();
        validateDataDefaultInternal(object, defaultDataNullable(object.getClass()), nulls);

        return nulls;
    }

    private static void validateDataDefaultInternal(Object object, Boolean defaultDataNullable, List<String> nulls) {
        validateDataDefaultInternal(object.getClass(), object, defaultDataNullable, nulls);
    }

    private static void validateDataDefaultInternal(Class<?> clazz, Object object, Boolean defaultDataNullable, List<String> nulls) {
        iterateFields(clazz, field -> {
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
                    } else {
                        validateDataDefaultInternalListOrArr(field, obj, defaultDataNullable, nulls);
                    }
                }

            } else {
                if (obj == null) {
                    if (field.isAnnotationPresent(NullableData.class)) {
                        return;
                    }
                    nulls.add(String.format("Field %s is null", field.getName()));
                } else {
                    validateDataDefaultInternalListOrArr(field, obj, defaultDataNullable, nulls);
                }
            }
        });
    }

    private static void iterateFields(Class<?> clazz, FieldConsumer action) {
        try {
            do {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    action.accept(field);
                }
                clazz = clazz.getSuperclass();
            } while (clazz != Object.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unexpected error while reflecting %s class", clazz.getSimpleName()), e);
        }
    }

    private interface FieldConsumer {
        void accept(Field field) throws Exception;
    }

    //this may or may not work, no config is currently using a list or string arr to store data
    private static void validateDataDefaultInternalListOrArr(Field field, Object data, Boolean defaultDataNullable, List<String> nulls) throws IllegalAccessException {
        //inner List objects with a generic type that implement NonNullData
        if (field.getType() == List.class) {
            List<?> list = (List<?>) field.get(data);
            for (Object o : list) {
                validateDataDefaultInternal(o, defaultDataNullable, nulls);
            }
        } else if (field.getType() == String[].class) {
            String[] arr = (String[]) field.get(data);
            for (Object o : arr) {
                validateDataDefaultInternal(o, defaultDataNullable, nulls);
            }
        }
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
