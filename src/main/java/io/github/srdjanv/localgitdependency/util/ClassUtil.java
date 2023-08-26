package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.util.annotations.NullableData;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClassUtil {
    private ClassUtil() {
    }

    public static <D> void instantiateObjectWithBuilder(final D object, final D builder, final Class<D> fieldsClazz) {
        iterateFields(fieldsClazz, field -> {
            field.set(object, field.get(builder));
        });
    }

    //if both objects have non-null data, the default is the reference
    public static <D> void mergeObjectsDefaultReference(final D newObject, final D referenceObject, final Class<D> clazz) {
        iterateFields(clazz, field -> {
            Object referenceObjectData = field.get(referenceObject);
            if (referenceObjectData != null)
                field.set(newObject, referenceObjectData);
        });
    }

    //if both objects have non-null data, the default is the newObject
    public static <D> void mergeObjectsDefaultNewObject(final D newObject, final D referenceObject, final Class<D> clazz) {
        iterateFields(clazz, field -> {
            if (field.get(newObject) == null)
                field.set(newObject, field.get(referenceObject));
        });
    }

    public static void iterateFields(Class<?> clazz, final FieldConsumer action) {
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
    public interface FieldConsumer {
        void accept(Field field) throws Exception;
    }

    public static <D> void finalizeProperties(final D targetObj, final Class<D> clazz) {
        iterateMethods(clazz, method -> {
            for (Class<?> anInterface : method.getReturnType().getInterfaces()) {
                if (anInterface == Property.class) {
                    ((Property<?>) method.invoke(targetObj)).finalizeValue();
                    return;
                }
            }
        }, Collections.singletonList(() -> ConfigFinalizer.class.getDeclaredMethod("finalizeProps")));
    }

    public static void iterateMethods(Class<?> clazz, final MethodConsumer action, final List<MethodSuppler> methodBlackList) {
        try {
            var resolvedMethodBlackList = new ArrayList<Method>();
            for (MethodSuppler methodSupplier : methodBlackList) {
                resolvedMethodBlackList.add(methodSupplier.get());
            }

            do {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (resolvedMethodBlackList.contains(method)) return;
                    method.setAccessible(true);
                    action.accept(method);
                }
                clazz = clazz.getSuperclass();
                if (clazz == null) break;
            } while (clazz != Object.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unexpected error while reflecting %s class", clazz.getSimpleName()), e);
        }
    }

    @FunctionalInterface
    public interface MethodSuppler {
        Method get() throws Exception;
    }

    @FunctionalInterface
    public interface MethodConsumer {
        void accept(Method method) throws Exception;
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
            throw new RuntimeException(String.format("Unexpected error while reflecting %s class", dataClazz.getSimpleName()), e);
        }
    }

    private static void validRawData(List<String> nulls, boolean clazzNullable, FieldDataPair fieldDataPair) {
        if (fieldDataPair.field.isAnnotationPresent(NullableData.class)) return;
        var doCheck = false;
        if (clazzNullable) {
            if (fieldDataPair.field.isAnnotationPresent(NotNull.class)) doCheck = true;
        } else doCheck = true;

        if (doCheck && fieldDataPair.data == null) nulls.add(fieldDataPair.field.getName() + " is null");
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
