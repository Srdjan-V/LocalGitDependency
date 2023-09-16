package io.github.srdjanv.localgitdependency.project;

import io.github.srdjanv.localgitdependency.logger.PluginLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class ManagerRunner<T extends Manager> {
    private Function<Managers, T> managerRunner;
    private ReflectionFunction<Class<T>, Method> methodFunction;
    private List<Predicate<Managers>> skipChecks;
    private RunLogType runLogType;
    private Method method;
    private String taskName;

    static <M extends Manager> ManagerRunner<M> create(Consumer<ManagerRunner<M>> managerConfigurator) {
        ManagerRunner<M> managerRunner = new ManagerRunner<>();
        managerConfigurator.accept(managerRunner);
        managerRunner.verify();
        return managerRunner;
    }

    private ManagerRunner() {}

    private void verify() {
        Objects.requireNonNull(managerRunner, "ManagerRunner is null");
        Objects.requireNonNull(methodFunction, "MethodFunction is null");
        Objects.requireNonNull(runLogType, "RunLogType is null");

        if (skipChecks == null) skipChecks = Collections.emptyList();
    }

    public void setManagerSupplier(Function<Managers, T> managerRunner) {
        this.managerRunner = managerRunner;
    }

    public void setTask(ReflectionFunction<Class<T>, Method> methodFunction) {
        this.methodFunction = methodFunction;
    }

    public void setRunLogType(RunLogType runLogType) {
        this.runLogType = runLogType;
    }

    public void addSkipCheck(Predicate<Managers> skipCheck) {
        if (skipChecks == null) skipChecks = new ArrayList<>();
        skipChecks.add(skipCheck);
    }

    public void runAndLog(Managers managers) {
        final T manager = managerRunner.apply(managers);
        if (skipChecks.stream().anyMatch(test -> test.test(manager))) return;
        oneTimeRuntimeSetup(manager);
        switch (runLogType) {
            case SILENT -> silent(manager);
            case MINIMAL -> minimal(manager);
        }
    }

    private void silent(T manager) {
        invokeMethod(manager);
    }

    private void minimal(T manager) {
        final long start = System.currentTimeMillis();

        var ret = invokeMethod(manager);
        if (ret != null) {
            if ((Boolean) ret)
                PluginLogger.task(
                        "{}: Finished {} in {} ms",
                        manager.getManagerName(),
                        taskName,
                        System.currentTimeMillis() - start);
            return;
        }
        PluginLogger.task(
                "{}: Finished {} in {} ms", manager.getManagerName(), taskName, System.currentTimeMillis() - start);
    }

    private Object invokeMethod(T manager) {
        try {
            return method.invoke(manager);
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
                if (declaredMethod.getParameterCount() != method.getParameterCount()) continue;

                if (declaredMethod.isAnnotationPresent(TaskDescription.class)) {
                    taskName =
                            declaredMethod.getAnnotation(TaskDescription.class).value();
                    if (declaredMethod.getReturnType() != boolean.class && declaredMethod.getReturnType() != void.class)
                        throw new RuntimeException(
                                String.format("Method: %s in not returning boolean or void", method.getName()));

                    return;
                } else {
                    throw new RuntimeException(String.format(
                            "Method with name %s is not annotated with %s",
                            method.getName(), TaskDescription.class.getSimpleName()));
                }
            }
        }
        throw new RuntimeException(String.format(
                "Method with name %s not found in class %s", method.getName(), Managers.class.getSimpleName()));
    }

    public interface ReflectionFunction<T, R> {
        R apply(T t) throws NoSuchMethodException;
    }

    public enum RunLogType {
        SILENT,
        MINIMAL
    }
}
