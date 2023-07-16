package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import io.github.srdjanv.localgitdependency.util.annotations.NullableData;

import java.io.File;

public abstract class DefaultableLauncherConfigFields {
    @NullableData
    protected File executable;
    protected Integer gradleDaemonMaxIdleTime;
    protected Boolean forwardOutput;
}
