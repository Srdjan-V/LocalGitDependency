package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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

    @NotNull
    public static List<String> validData(Object data) {
        List<String> nulls = new ArrayList<>(0);
        validDataInternal(nulls, data.getClass(), data);

        return nulls;
    }

    @NotNull
    public static List<String> validData(Class<?> dataClazz, Object data) {
        List<String> nulls = new ArrayList<>(0);
        validDataInternal(nulls, dataClazz, data);

        return nulls;
    }


    private static void validDataInternal(List<String> nulls, Class<?> dataClazz, Object data) {
        try {
            do {
                boolean clazzNullable = dataClazz.isAnnotationPresent(NullableData.class);
                var fields = dataClazz.getDeclaredFields();
                for (Field declaredField : fields)
                    validRawData(nulls, clazzNullable, declaredField, data);

                for (Field declaredField : fields)
                    validIterableData(nulls, declaredField, data);

                dataClazz = dataClazz.getSuperclass();
            } while (dataClazz != Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void validRawData(List<String> nulls, boolean clazzNullable, Field field, Object data) throws IllegalAccessException {
        if (field.isAnnotationPresent(NullableData.class)) return;
        field.setAccessible(true);
        var fData = field.get(data);
        if (fData == null && !clazzNullable) nulls.add(field.getName() + " is null");
    }

    private static void validIterableData(List<String> nulls, Field field, Object data) throws IllegalAccessException {
        if (field.getDeclaringClass() != Iterable.class) return;
        field.setAccessible(true);
        var fData = field.get(data);
        if (fData == null) return;

        for (Object iData : (Iterable<?>) fData) validDataInternal(nulls, iData.getClass(), iData);
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

}
