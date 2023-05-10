package io.github.srdjanv.localgitdependency.util;

import groovy.lang.Closure;

public final class ClosureUtil {
    private ClosureUtil() {
    }

    public static void delegate(Closure closure, Object delegate) {
        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

}
