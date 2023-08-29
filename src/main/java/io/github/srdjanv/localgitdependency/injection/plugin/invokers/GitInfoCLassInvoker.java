package io.github.srdjanv.localgitdependency.injection.plugin.invokers;

import io.github.srdjanv.localgitdependency.git.GitInfo;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import org.jetbrains.annotations.Nullable;

public class GitInfoCLassInvoker {
    private final Class<GitInfo> clazz;
    private final MethodHandle method$getDir;

    public static GitInfoCLassInvoker createInvoker(
            MethodHandles.Lookup lookup, @Nullable GitInfoCLassInvoker invoker, DependencyClassInvoker depInvoker)
            throws Throwable {
        var info = depInvoker.getGitInfo();
        var clazz = info.getClass();

        if (invoker == null) {
            invoker = new GitInfoCLassInvoker(lookup, clazz);
        } else if (!invoker.clazz.equals(clazz)) {
            invoker = new GitInfoCLassInvoker(lookup, clazz);
        }

        invoker.gitInfo = info;
        return invoker;
    }

    public GitInfoCLassInvoker(MethodHandles.Lookup lookup, Class<?> gitInfoClazz)
            throws NoSuchMethodException, IllegalAccessException {
        this.clazz = (Class<GitInfo>) gitInfoClazz;

        method$getDir = lookup.unreflect(clazz.getDeclaredMethod("getDir"));
    }

    private Object gitInfo;

    public File getDir() throws Throwable {
        return (File) method$getDir.invoke(gitInfo);
    }
}
