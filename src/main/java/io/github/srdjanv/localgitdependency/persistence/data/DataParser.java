package io.github.srdjanv.localgitdependency.persistence.data;

import com.google.gson.*;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeDataGetters;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataParser {
    private DataParser() {
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ProjectProbeDataGetters parseJson(String json) {
        try {
            ProjectProbeData data = gson.fromJson(json, ProjectProbeData.class);
            if (validDataForClass(ProjectProbeData.class, data)) {
                return data;
            }
        } catch (JsonSyntaxException ignore) {
        }
        return new ProjectProbeData();
    }

    public static String projectProbeDataJson(ProjectProbeData projectProbeData) {
        if (validDataForClass(ProjectProbeData.class, projectProbeData)) {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(projectProbeData);
        }

        throw new RuntimeException();
    }

    public static boolean validDataForClass(Class<?> clazz, Object data) {
        if(data == null) {
            return false;
        }

        if (!isClassImplementingNonNullData(clazz)) {
            return true;
        }

        try {
            for (Field declaredField : clazz.getDeclaredFields()) {
                declaredField.setAccessible(true);

                //simple data for class like string
                if (declaredField.get(data) == null) {
                    return false;
                }

                //inner objects that implement NonNullData
                if (!validDataForClass(declaredField.getType(), declaredField.get(data))) {
                    return false;
                }

                //inner List objects with a generic type that implement NonNullData
                if (declaredField.getType() == List.class) {
                    Type genericType = declaredField.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        Type type = parameterizedType.getActualTypeArguments()[0];
                        if (type instanceof Class) {
                            Class<?> listClazz = (Class<?>) type;
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
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static boolean isClassImplementingNonNullData(Class<?> clazz) {
        for (Class<?> clazzInterface : clazz.getInterfaces()) {
            if (clazzInterface == NonNullData.class) {
                return true;
            }
        }
        return false;
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
            } catch (IllegalStateException ignore){
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
