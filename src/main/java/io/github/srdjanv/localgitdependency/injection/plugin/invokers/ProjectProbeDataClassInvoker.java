package io.github.srdjanv.localgitdependency.injection.plugin.invokers;

import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

public class ProjectProbeDataClassInvoker {
    private final Class<ProjectProbeData> clazz;
    private final MethodHandle method$getProjectID;
    private final MethodHandle method$getArchivesBaseName;
    private final MethodHandle method$getSubDependencyData;

    public static ProjectProbeDataClassInvoker createInvoker(MethodHandles.Lookup lookup,
                                                             @Nullable ProjectProbeDataClassInvoker invoker,
                                                             PersistentInfoClassInvoker persistentInvoker) throws Throwable {

        var probeData = persistentInvoker.getProbeData();
        if (invoker == null) {
            invoker = new ProjectProbeDataClassInvoker(lookup, probeData.getClass());

        } else if (!invoker.clazz.equals(probeData.getClass())) {
            invoker = new ProjectProbeDataClassInvoker(lookup, probeData.getClass());
        }

        invoker.probeData = probeData;
        return invoker;
    }

    public ProjectProbeDataClassInvoker(MethodHandles.Lookup lookup, Class<?> dataClazz) throws NoSuchMethodException, IllegalAccessException {
        this.clazz = (Class<ProjectProbeData>) dataClazz;

        method$getProjectID = lookup.unreflect(clazz.getDeclaredMethod("getProjectID"));
        method$getArchivesBaseName = lookup.unreflect(clazz.getDeclaredMethod("getArchivesBaseName"));
        method$getSubDependencyData = lookup.unreflect(clazz.getDeclaredMethod("getSubDependencyData"));
    }

    private Object probeData;

    public String getProjectID() throws Throwable {
        return (String) method$getProjectID.invoke(probeData);
    }

    public String getArchivesBaseName() throws Throwable {
        return (String) method$getArchivesBaseName.invoke(probeData);
    }

    public Collection<Object> getSubDependencyData() throws Throwable {
        return (Collection<Object>) method$getSubDependencyData.invoke(probeData);
    }
}
