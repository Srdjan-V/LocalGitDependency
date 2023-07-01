package io.github.srdjanv.localgitdependency.config;

import io.github.srdjanv.localgitdependency.config.impl.plugin.PluginConfig;
import org.gradle.internal.impldep.com.google.common.base.Function;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PluginConfigMapper<T> {
    private String name;
    private Function<PluginConfig, Object> valueGetter;
    private Function<PluginConfig, Object> newValue;
    private BiConsumer<PluginConfig.Builder, Object> builderConfig;

    public static <T> PluginConfigMapper<T> create(List<PluginConfigMapper<?>> mappers, Consumer<PluginConfigMapper<T>> config) {
        var mapper = new PluginConfigMapper<T>();
        config.accept(mapper);
        mappers.add(mapper);
        return mapper;
    }

    private PluginConfigMapper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @SuppressWarnings("unchecked")
    public void setValueGetter(Function<PluginConfig, T> valueGetter) {
        this.valueGetter = (Function<PluginConfig, Object>) valueGetter;
    }

    @SuppressWarnings("unchecked")
    public void setNewValue(Function<PluginConfig, T> newValue) {
        this.newValue = (Function<PluginConfig, Object>) newValue;
    }

    @SuppressWarnings("unchecked")
    public void setBuilder(BiConsumer<PluginConfig.Builder, T> builderConfig) {
        this.builderConfig = (BiConsumer<PluginConfig.Builder, Object>) builderConfig;
    }

    public Function<PluginConfig, Object> getValueGetter() {
        return valueGetter;
    }

    public Function<PluginConfig, Object> getNewValue() {
        return newValue;
    }

    public BiConsumer<PluginConfig.Builder, Object> getBuilderConfig() {
        return builderConfig;
    }
}
