package io.github.srdjanv.localgitdependency.config;

import java.util.function.BiConsumer;
import org.gradle.internal.impldep.com.google.common.base.Function;

public abstract class ConfigMapper<C, B, T> {
    private String name;
    private Function<C, T> valueGetter;
    private Function<B, T> valueSetter;
    private Function<C, T> newValue;
    private BiConsumer<B, T> builderConfig;

    protected ConfigMapper() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueGetter(Function<C, T> valueGetter) {
        this.valueGetter = valueGetter;
    }

    public void setNewValue(Function<C, T> newValue) {
        this.newValue = newValue;
    }

    public void setBuilder(BiConsumer<B, T> builderConfig) {
        this.builderConfig = builderConfig;
    }

    @SuppressWarnings("unchecked")
    public Function<C, Object> getValueGetter() {
        return (Function<C, Object>) valueGetter;
    }

    @SuppressWarnings("unchecked")
    public Function<C, Object> getNewValue() {
        return (Function<C, Object>) newValue;
    }

    @SuppressWarnings("unchecked")
    public BiConsumer<B, Object> getBuilderConfig() {
        return (BiConsumer<B, Object>) builderConfig;
    }

    public Function<C, T> getValueGetterType() {
        return valueGetter;
    }

    public Function<C, T> getNewValueType() {
        return newValue;
    }

    public BiConsumer<B, T> getBuilderConfigType() {
        return builderConfig;
    }
}
