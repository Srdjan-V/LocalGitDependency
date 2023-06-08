package io.github.srdjanv.localgitdependency.persistence.data;

import com.google.gson.*;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static io.github.srdjanv.localgitdependency.util.ClassUtil.isClassAnnotatedWithNonNullData;

public class DataParser {
    private DataParser() {
    }

    public static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public static ProjectProbeData parseJson(String json) {
        ProjectProbeData data = gson.fromJson(json, ProjectProbeData.class);
        if (validDataForClass(ProjectProbeData.class, data)) {
            return data;
        }
        throw new RuntimeException("Invalid gradle probe data");
    }

    public static String projectProbeDataJson(ProjectProbeData projectProbeData) {
        if (validDataForClass(ProjectProbeData.class, projectProbeData)) {
            Gson gson;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            return gson.toJson(projectProbeData);
        }

        throw new IllegalStateException("Incomplete data");
    }

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

    public static List<DataWrapper> complexLoadDataFromFileJson(File file, DataLayout layout) {
        boolean fileExits = file.exists();
        ArrayList<DataWrapper> arrayList = new ArrayList<>();
        JsonArray jsonArray = null;

        if (fileExits) {
            JsonElement jsonElement;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                jsonElement = JsonParser.parseReader(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                jsonArray = jsonElement.getAsJsonArray();
            } catch (IllegalStateException ignore) {
                for (DataLayout.DataMapper<?> dataMapper : layout.getDataMappers()) {
                    arrayList.add(DataWrapper.create(dataMapper));
                }
                return arrayList;
            }
        }

        for (DataLayout.DataMapper<?> dataMapper : layout.getDataMappers()) {
            if (!fileExits) {
                arrayList.add(DataWrapper.create(dataMapper));
                continue;
            }

            try {
                Object data = gson.fromJson(jsonArray.get(dataMapper.getInstanceIndex()), dataMapper.getClazz());
                if (validDataForClass(dataMapper.getClazz(), data)) {
                    arrayList.add(DataWrapper.create(dataMapper, data));
                } else {
                    arrayList.add(DataWrapper.create(dataMapper));
                }
            } catch (JsonSyntaxException ignore) {
                arrayList.add(DataWrapper.create(dataMapper));
            }
        }

        return arrayList;
    }

    public static void complexSaveDataToFileJson(File file, List<?> data, DataLayout layout) {
        data.sort(layout);
        simpleSaveDataToFileJson(file, data);
    }

    public static <T> T simpleLoadDataFromFileJson(File file, Class<T> clazz, Supplier<T> instanceSupplier) {
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                T data = gson.fromJson(reader, clazz);
                if (validDataForClass(clazz, data)) {
                    return data;
                } else {
                    return instanceSupplier.get();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instanceSupplier.get();
    }

    public static void simpleSaveDataToFileJson(File file, Object data) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.write(gson.toJson(data));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
