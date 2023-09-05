package io.github.srdjanv.localgitdependency.injection.plugin.invokers;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import org.jetbrains.annotations.Nullable;

public class DependencyClassInvoker {
    private final Class<Dependency> depClazz;
    private final MethodHandle method$getName;
    private final MethodHandle method$getGitInfo;
    private final MethodHandle method$getPersistentInfo;

    public static DependencyClassInvoker createInvoker(
            MethodHandles.Lookup lookup, @Nullable DependencyClassInvoker invoker, Object depObj) throws Throwable {
        var depClazz = depObj.getClass();
        if (invoker == null) {
            invoker = new DependencyClassInvoker(lookup, depClazz);
        } else if (!invoker.depClazz.equals(depClazz)) {
            invoker = new DependencyClassInvoker(lookup, depClazz);
        }

        invoker.depObj = depObj;
        return invoker;
    }

    private DependencyClassInvoker(MethodHandles.Lookup lookup, Class<?> clazz) throws Throwable {
        this.depClazz = (Class<Dependency>) clazz;

        method$getName = lookup.unreflect(depClazz.getDeclaredMethod("getName"));
        method$getGitInfo = lookup.unreflect(depClazz.getDeclaredMethod("getGitInfo"));
        method$getPersistentInfo = lookup.unreflect(depClazz.getDeclaredMethod("getPersistentInfo"));
    }

    private Object depObj;

    public String getName() throws Throwable {
        return (String) method$getName.invoke(depObj);
    }

    public Object getGitInfo() throws Throwable {
        return method$getGitInfo.invoke(depObj);
    }

    public Object getPersistentInfo() throws Throwable {
        return method$getPersistentInfo.invoke(depObj);
    }
}
