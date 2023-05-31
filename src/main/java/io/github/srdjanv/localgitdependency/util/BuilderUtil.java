package io.github.srdjanv.localgitdependency.util;

import java.lang.reflect.Field;

public final class BuilderUtil {
    private BuilderUtil() {
    }

    public static <D> void instantiateObjectWithBuilder(D object, D builder, final Class<D> fieldsClazz) {
        Class<?> currentClazz = fieldsClazz;
        do {
            for (Field field : currentClazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    var builderObj = field.get(builder);
                    if (builderObj != null) {
                        field.set(object, builderObj);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unexpected error while reflecting %s class", currentClazz.getSimpleName()), e);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != Object.class);
    }

}
