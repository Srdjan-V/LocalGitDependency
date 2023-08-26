package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public final class DefaultLaunchers {

    public abstract static class Startup extends Base implements Launchers.Startup {
        @Inject
        public Startup(Managers managers) {
            super(managers);
        }
    }

    public abstract static class Probe extends Base implements Launchers.Probe {
        @Inject
        public Probe(Managers managers) {
            super(managers);
            getTaskTriggers().convention(Arrays.asList("settings.gradle", "build.gradle", "gradle.properties"));
            getMainTasksArguments().convention(managers.getProject().provider(() -> {
                File mainInit = Constants.concatFile.apply(Constants.lgdDir.apply(managers.getProject()).getAsFile(), Constants.MAIN_INIT_SCRIPT_GRADLE);
                return Arrays.asList("--init-script", mainInit.getAbsolutePath());
            }));
        }
    }

    public abstract static class Build extends Base implements Launchers.Build{
        @Inject
        public Build(Managers managers) {
            super(managers);
            getMainTasksArguments().convention(managers.getProject().provider(() -> {
                return Arrays.asList("--init-script", dependencyProperty.get().getGradleInfo().getInitScript().getAbsolutePath());
            }));
            getMainTasks().convention(Collections.singletonList("build"));
        }
    }

    public abstract static class Base extends GroovyObjectSupport implements Launchers.Base, ConfigFinalizer {
        protected Property<Dependency> dependencyProperty;
        protected Property<Boolean> isRunNeeded;

        public Base(Managers managers) {
            dependencyProperty = managers.getProject().getObjects().property(Dependency.class);
        }

        public Property<Dependency> getDependencyProperty() {
            return dependencyProperty;
        }

        public Property<Boolean> getIsRunNeeded() {
            return isRunNeeded;
        }

        @Override
        public void finalizeProps() {

        }
    }

    private DefaultLaunchers() {
    }
}
