package io.github.srdjanv.localgitdependency.util.dsl;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.ProjectInstance;
import io.github.srdjanv.localgitdependency.property.DefaultProperty;
import io.github.srdjanv.localgitdependency.property.PropertyManager;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class GlobalToGlobalMapper extends BaseMapper {
    private Consumer<GlobalToGlobalMapper> testRunner;
    private BiConsumer<Object, Object> assertion;
    private Method oldGlobalConfig;
    private BiConsumer<Object, DefaultProperty.Builder> newGlobalConfig;
    private Function<Object, Object> resolveNewGlobalConfig;

    private GlobalToGlobalMapper() {
        testRunner = mapper -> {
            ProjectInstance.createProject();
            PropertyManager property = Instances.getPropertyManager();
            Object defaultValue = mapper.getOldGlobalConfig().apply(property.getGlobalProperty());
            Object newValue = mapper.getResolveNewGlobalConfig().apply(defaultValue);

            Closure<DefaultProperty.Builder> globalClosure = new Closure<DefaultProperty.Builder>(null) {
                public DefaultProperty.Builder doCall() {
                    DefaultProperty.Builder builder = (DefaultProperty.Builder) getDelegate();
                    mapper.getNewGlobalConfig().accept(newValue, builder);
                    return builder;
                }
            };

            property.globalProperty(globalClosure);
            assertion.accept(newValue, mapper.getOldGlobalConfig().apply(property.getGlobalProperty()));
        };

        assertion = Assertions::assertEquals;
    }

    public static GlobalToGlobalMapper configure(Consumer<GlobalToGlobalMapper> consumer) {
        GlobalToGlobalMapper globalToGlobalMapper = new GlobalToGlobalMapper();
        consumer.accept(globalToGlobalMapper);
        return globalToGlobalMapper;
    }

    public void getOldGlobalConfig(Method oldGlobalConfig) {
        this.oldGlobalConfig = oldGlobalConfig;
    }

    public void setNewGlobalConfig(BiConsumer<Object, DefaultProperty.Builder> newGlobalConfig) {
        this.newGlobalConfig = newGlobalConfig;
    }

    public void resolveNewGlobalConfig(Function<Object, Object> resolveNewGlobalConfig) {
        this.resolveNewGlobalConfig = resolveNewGlobalConfig;
    }

    public void setCustomTestRunner(Consumer<GlobalToGlobalMapper> testRunner) {
        this.testRunner = testRunner;
    }

    @Override
    public void runTest() {
        testRunner.accept(this);
    }

    public void setAssertion(BiConsumer<Object, Object> assertion) {
        this.assertion = assertion;
    }

    @Override
    public String getTestName() {
        return oldGlobalConfig.getName();
    }

    public Function<DefaultProperty, Object> getOldGlobalConfig() {
        return (property -> {
            try {
                return oldGlobalConfig.invoke(property);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public BiConsumer<Object, DefaultProperty.Builder> getNewGlobalConfig() {
        return newGlobalConfig;
    }

    public Function<Object, Object> getResolveNewGlobalConfig() {
        return resolveNewGlobalConfig;
    }
}
