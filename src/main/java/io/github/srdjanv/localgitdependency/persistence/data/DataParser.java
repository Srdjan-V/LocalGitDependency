package io.github.srdjanv.localgitdependency.persistence.data;

import static io.github.srdjanv.localgitdependency.util.ClassUtil.validData;

import com.google.gson.*;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataParser {
    private DataParser() {}

    public static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public static ProjectProbeData parseJson(String json) {
        ProjectProbeData data = gson.fromJson(json, ProjectProbeData.class);
        var nulls = validData(ProjectProbeData.class, data);
        if (nulls.isEmpty()) return data;
        throw ErrorUtil.create("Invalid gradle probe data:").append(nulls).toRuntimeException();
    }

    public static String projectProbeDataJson(ProjectProbeData projectProbeData) {
        var nulls = validData(ProjectProbeData.class, projectProbeData);
        if (nulls.isEmpty()) {
            Gson gson;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            return gson.toJson(projectProbeData);
        }
        throw ErrorUtil.create("Incomplete data:").append(nulls).toRuntimeException();
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
                if (validData(dataMapper.getClazz(), data).isEmpty()) {
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
                if (validData(clazz, data).isEmpty()) {
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
