package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir.FlatDirRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy.IvyRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven.MavenRepository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


public class RepositoryTypeAdapter extends TypeAdapter<Repository> {
    private static final String typeName;
    static {
        try {
            Field field = Repository.class.getDeclaredField("type");
            typeName = field.getName();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<?> rawType = typeToken.getRawType();
            if (!Repository.class.isAssignableFrom(rawType)) {
                return null;
            }

            @SuppressWarnings({"unchecked"})
            TypeAdapter<T> arrayAdapter = (TypeAdapter<T>) new RepositoryTypeAdapter(gson).nullSafe();
            return arrayAdapter;
        }
    };

    private final TypeAdapter<JsonElement> jsonElementAdapter;
    final Map<String, TypeAdapter<? extends Repository>> delegateMap = new LinkedHashMap<>();

    public RepositoryTypeAdapter(Gson gson) {
        jsonElementAdapter = gson.getAdapter(JsonElement.class);

        delegateMap.put(Constants.Maven, gson.getDelegateAdapter(FACTORY, TypeToken.get(MavenRepository.class)));
        delegateMap.put(Constants.Ivy, gson.getDelegateAdapter(FACTORY, TypeToken.get(IvyRepository.class)));
        delegateMap.put(Constants.FlatDir, gson.getDelegateAdapter(FACTORY, TypeToken.get(FlatDirRepository.class)));
    }

    @Override
    public void write(JsonWriter out, Repository value) throws IOException {
        @SuppressWarnings("unchecked")
        TypeAdapter<Repository> delegate = (TypeAdapter<Repository>) delegateMap.get(value.getType());

        if (delegate == null) {
            throw new IllegalStateException("Unknown repository type");
        }

        JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
        jsonElementAdapter.write(out, jsonObject);
    }

    @Override
    public Repository read(JsonReader in) throws IOException {
        JsonElement jsonElement = jsonElementAdapter.read(in);
        String type = jsonElement.getAsJsonObject().get(typeName).getAsString();

        @SuppressWarnings("unchecked")
        TypeAdapter<Repository> delegate = (TypeAdapter<Repository>) delegateMap.get(type);

        if (delegate == null) {
            throw new IllegalStateException("Unknown repository type");
        }

        return delegate.fromJsonTree(jsonElement);
    }
}
