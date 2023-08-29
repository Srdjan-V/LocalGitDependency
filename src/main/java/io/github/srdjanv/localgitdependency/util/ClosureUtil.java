package io.github.srdjanv.localgitdependency.util;

import groovy.lang.Closure;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClosureUtil {
    private ClosureUtil() {}

    public static boolean delegateNullSafe(@Nullable Closure closure, @NotNull Object delegate) {
        if (closure != null) {
            delegate(closure, delegate);
            return true;
        }
        return false;
    }

    public static void delegate(@NotNull Closure closure, @NotNull Object delegate) {
        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    public static <T> Closure<T> of(Supplier<T> supplier) {
        return new Closure<>(ClosureUtil.class) {
            @Override
            public T call(Object... args) {
                return supplier.get();
            }
        };
    }

    public static <T> Closure<T> configure(Consumer<T> consumer) {
        return new Closure<>(ClosureUtil.class) {
            @Override
            public T call(Object... args) {
                T delegate = ((T) getDelegate());
                consumer.accept(delegate);
                return delegate;
            }
        };
    }
}
