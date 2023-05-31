package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.logger.PluginLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

final class ManagerRunner<T extends Manager> {
    private Function<Managers, T> managerRunner;
    private ReflectionFunction<Class<T>, Method> methodFunction;
    private Method method;
    private String taskName;

    static <M extends Manager> ManagerRunner<M> create(Consumer<ManagerRunner<M>> managerConfigurator) {
        ManagerRunner<M> managerRunner = new ManagerRunner<>();
        managerConfigurator.accept(managerRunner);
        managerRunner.verify();
        return managerRunner;
    }

    private ManagerRunner() {
    }

    private void verify() {
        if (managerRunner == null) {
            throw new NullPointerException("ManagerRunner is null");
        }
        if (methodFunction == null) {
            throw new NullPointerException("MethodFunction is null");
        }
    }

    public void setManagerSupplier(Function<Managers, T> managerRunner) {
        this.managerRunner = managerRunner;
    }

    public void setTask(ReflectionFunction<Class<T>, Method> methodFunction) {
        this.methodFunction = methodFunction;
    }

    public void runAndLog(Managers managers) {
        final long start = System.currentTimeMillis();
        T manager = managerRunner.apply(managers);
        oneTimeRuntimeSetup(manager);

        PluginLogger.info("{}: Started {}", manager.getManagerName(), taskName);
        invokeMethod(manager);
        final long spent = System.currentTimeMillis() - start;
        PluginLogger.info("{}: Finished {} in {} ms", manager.getManagerName(), taskName, spent);
    }

    private void invokeMethod(T manager) {
        try {
            method.invoke(manager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private void oneTimeRuntimeSetup(T manager) {
        if (method != null) return;

        try {
            method = methodFunction.apply((Class) manager.getClass());
            method.setAccessible(true);
            methodFunction = null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        for (Class<?> anInterface : manager.getClass().getInterfaces()) {
            for (Method declaredMethod : anInterface.getDeclaredMethods()) {
                if (!declaredMethod.getName().equals(method.getName())) continue;

                if (declaredMethod.isAnnotationPresent(TaskDescription.class)) {
                    taskName = declaredMethod.getAnnotation(TaskDescription.class).value();
                    return;
                } else {
                    throw new RuntimeException(String.format("Method with name %s is not annotated with %s", method.getName(), TaskDescription.class.getSimpleName()));
                }
            }
        }
        throw new RuntimeException(String.format("Method with name %s not found in class %s", method.getName(), Managers.class.getSimpleName()));
    }

    public interface ReflectionFunction<T, R> {
        R apply(T t) throws NoSuchMethodException;
    }
}
