package io.github.srdjanv.localgitdependency.injection.plugin.invokers;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class SubDependencyClassInvoker {
    private final Class<SubDependencyData> clazz;

    private final MethodHandle method$getName;
    private final MethodHandle method$getProjectID;
    private final MethodHandle method$getArchivesBaseName;
    private final MethodHandle method$getDependencyType;
    private final MethodHandle method$getGitDir;

    public static SubDependencyClassInvoker createInvoker(MethodHandles.Lookup lookup, @Nullable SubDependencyClassInvoker invoker, Object subDep) throws Throwable {
        var clazz = subDep.getClass();
        if (invoker == null) {
            invoker = new SubDependencyClassInvoker(lookup, clazz);
        } else if (!invoker.clazz.equals(clazz)) {
            invoker = new SubDependencyClassInvoker(lookup, clazz);
        }

        invoker.subDep = subDep;
        return invoker;
    }

    private SubDependencyClassInvoker(MethodHandles.Lookup lookup, Class<?> clazz) throws Throwable {
        this.clazz = (Class<SubDependencyData>) clazz;
        method$getName = lookup.unreflect(clazz.getDeclaredMethod("getName"));
        method$getProjectID = lookup.unreflect(clazz.getDeclaredMethod("getProjectID"));
        method$getArchivesBaseName = lookup.unreflect(clazz.getDeclaredMethod("getArchivesBaseName"));
        method$getDependencyType = lookup.unreflect(clazz.getDeclaredMethod("getDependencyType"));
        method$getGitDir = lookup.unreflect(clazz.getDeclaredMethod("getGitDir"));
    }

    private Object subDep;

    public String getName() throws Throwable {
        return (String) method$getName.invoke(subDep);
    }

    public String getProjectID() throws Throwable {
        return (String) method$getProjectID.invoke(subDep);
    }

    public String getArchivesBaseName() throws Throwable {
        return (String) method$getArchivesBaseName.invoke(subDep);
    }

    public Dependency.Type getDependencyType() throws Throwable {
        return Dependency.Type.values()[((Enum) method$getDependencyType.invoke(subDep)).ordinal()];
    }

    public String getGitDir() throws Throwable {
        return (String) method$getGitDir.invoke(subDep);
    }
}
