package io.github.srdjanv.localgitdependency.injection.plugin.invokers;

import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class PersistentInfoClassInvoker {
    private final Class<PersistentInfo> clazz;
    private final MethodHandle method$getProbeData;

    public static PersistentInfoClassInvoker createInvoker(MethodHandles.Lookup lookup,
                                                           @Nullable PersistentInfoClassInvoker invoker,
                                                           DependencyClassInvoker depInvoker) throws Throwable {

        var persistentInfo = depInvoker.getPersistentInfo();
        if (invoker == null) {
            invoker = new PersistentInfoClassInvoker(lookup, persistentInfo.getClass());
        } else if (!invoker.clazz.equals(persistentInfo.getClass())) {
            invoker = new PersistentInfoClassInvoker(lookup, persistentInfo.getClass());
        }

        invoker.info = persistentInfo;
        return invoker;
    }

    public PersistentInfoClassInvoker(MethodHandles.Lookup lookup, Class<?> dataClazz) throws NoSuchMethodException, IllegalAccessException {
        this.clazz = (Class<PersistentInfo>) dataClazz;

        method$getProbeData = lookup.unreflect(clazz.getDeclaredMethod("getProbeData"));
    }


    private Object info;

    public Object getProbeData() throws Throwable {
        return method$getProbeData.invoke(info);
    }
}
