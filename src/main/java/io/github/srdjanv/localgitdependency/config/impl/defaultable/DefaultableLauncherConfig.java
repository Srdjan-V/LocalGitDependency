package io.github.srdjanv.localgitdependency.config.impl.defaultable;

import io.github.srdjanv.localgitdependency.config.dependency.defaultable.DefaultableLauncherBuilder;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;

public class DefaultableLauncherConfig extends DefaultableLauncherConfigFields {
    public DefaultableLauncherConfig(Builder builder) {
        ClassUtil.instantiateObjectWithBuilder(this, builder, DefaultableLauncherConfigFields.class);
        if (builder.executable != null) {
            executable = FileUtil.toFile(builder.executable, "setExecutable");
        }
    }

    public DefaultableLauncherConfig(Builder builder, DefaultableLauncherConfig defaultConfig) {
        ClassUtil.instantiateObjectWithBuilder(this, defaultConfig, DefaultableLauncherConfigFields.class);
        ClassUtil.mergeObjectsDefaultNewObject(this, builder, DefaultableLauncherConfigFields.class);
    }

    public static class Builder extends DefaultableLauncherConfigFields implements DefaultableLauncherBuilder {
        private Object executable;

        @Override
        public void setExecutable(Object path) {
            this.executable = path;
        }

        @Override
        public void gradleDaemonMaxIdleTime(Integer gradleDaemonMaxIdleTime) {
            this.gradleDaemonMaxIdleTime = gradleDaemonMaxIdleTime;
        }

        @Override
        public void forwardOutput(Boolean forwardOutput) {
            this.forwardOutput = forwardOutput;
        }
    }
}
