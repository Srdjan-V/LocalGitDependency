package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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

    @FunctionalInterface
    private interface FieldConsumer {
        void accept(Field field) throws Exception;
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
                var resolvedFields = resolveFields(dataClazz, data);
                for (var resolvedData : resolvedFields)
                    validRawData(nulls, clazzNullable, resolvedData);

                for (var resolvedData : resolvedFields)
                    validIterableData(nulls, resolvedData);

                dataClazz = dataClazz.getSuperclass();
            } while (dataClazz != Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void validRawData(List<String> nulls, boolean clazzNullable, FieldDataPair fieldDataPair) {
        if (fieldDataPair.field.isAnnotationPresent(NullableData.class)) return;
        if (fieldDataPair.data == null && !clazzNullable) nulls.add(fieldDataPair.field.getName() + " is null");
    }

    private static void validIterableData(List<String> nulls, FieldDataPair fieldDataPair) {
        if (fieldDataPair.field.getDeclaringClass() != Iterable.class) return;
        if (fieldDataPair.data == null) return;

        for (Object iData : (Iterable<?>) fieldDataPair.data) validDataInternal(nulls, iData.getClass(), iData);
    }

    private static List<FieldDataPair> resolveFields(Class<?> dataClazz, Object data) throws IllegalAccessException {
        var fields = dataClazz.getDeclaredFields();
        if (fields.length == 0) return Collections.emptyList();

        var resolvedFields = new ArrayList<FieldDataPair>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            resolvedFields.add(FieldDataPair.create(field, field.get(data)));
        }
        return resolvedFields;
    }

    private static class FieldDataPair {
        private final Field field;
        private final Object data;

        public static FieldDataPair create(Field field, Object data) {
            return new FieldDataPair(field, data);
        }

        private FieldDataPair(Field field, Object data) {
            this.field = field;
            this.data = data;
        }
    }
}
