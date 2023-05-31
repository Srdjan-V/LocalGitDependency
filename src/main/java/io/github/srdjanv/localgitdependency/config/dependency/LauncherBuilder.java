package io.github.srdjanv.localgitdependency.config.dependency;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface LauncherBuilder {
    //void setExecutable(Object path);

    //void setJavaLauncher(JavaLauncher launcher);

    void startup(@DelegatesTo(value = Launchers.Startup.class,
            strategy = Closure.DELEGATE_FIRST) Closure startup);

    void probe(@DelegatesTo(value = Launchers.Probe.class,
            strategy = Closure.DELEGATE_FIRST) Closure probe);

    void build(@DelegatesTo(value = Launchers.Build.class,
            strategy = Closure.DELEGATE_FIRST) Closure build);
}
