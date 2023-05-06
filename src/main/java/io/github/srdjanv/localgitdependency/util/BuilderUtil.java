package io.github.srdjanv.localgitdependency.util;

import java.lang.reflect.Field;

public final class BuilderUtil {
    private BuilderUtil() {
    }

    public static <D> void instantiateObjectWithBuilder(D object, D builder, Class<D> fieldsClazz) {
        for (Field field : fieldsClazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(object, field.get(builder));
            } catch (Exception e) {
                throw new RuntimeException(String.format("Unexpected error while reflecting %s class", fieldsClazz), e);
            }
        }
    }

}
