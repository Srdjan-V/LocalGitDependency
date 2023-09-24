package io.github.srdjanv.localgitdependency.persistence.data;

import static io.github.srdjanv.localgitdependency.util.ClassUtil.validData;

import com.google.gson.*;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        JsonArray jsonArray;

        if (file.exists()) {
            JsonElement jsonElement;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                jsonElement = JsonParser.parseReader(reader);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            try {
                jsonArray = jsonElement.getAsJsonArray();
            } catch (IllegalStateException ignore) {
                return layout.getDataMappers().stream().map(DataWrapper::create).collect(Collectors.toList());
            }
        } else return layout.getDataMappers().stream().map(DataWrapper::create).collect(Collectors.toList());

        List<DataWrapper> wrappers = new ArrayList<>();
        for (DataLayout.DataMapper<?> dataMapper : layout.getDataMappers()) {
            try {
                Object data = gson.fromJson(jsonArray.get(dataMapper.getInstanceIndex()), dataMapper.getClazz());
                if (validData(dataMapper.getClazz(), data).isEmpty()) {
                    wrappers.add(DataWrapper.create(dataMapper, data));
                } else wrappers.add(DataWrapper.create(dataMapper));
            } catch (JsonSyntaxException | IndexOutOfBoundsException ignore) {
                wrappers.add(DataWrapper.create(dataMapper));
            }
        }
        return wrappers;
    }

    public static void complexSaveDataToFileJson(File file, List<?> data, DataLayout layout) {
        data.sort(layout);
        simpleSaveDataToFileJson(file, data);
    }

    public static <T> T simpleLoadDataFromFileJson(File file, Class<T> clazz, Supplier<T> instanceSupplier) {
        if (!file.exists()) return instanceSupplier.get();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            T data = gson.fromJson(reader, clazz);
            if (validData(clazz, data).isEmpty()) {
                return data;
            } else return instanceSupplier.get();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void simpleSaveDataToFileJson(File file, Object data) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.write(gson.toJson(data));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
