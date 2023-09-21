package io.github.srdjanv.localgitdependency.config.dependency.impl;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.ConfigFinalizer;
import io.github.srdjanv.localgitdependency.config.dependency.Launchers;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ClassUtil;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import javax.inject.Inject;
import org.gradle.api.provider.Property;

public final class DefaultLaunchers {

    public abstract static class Startup extends Base implements Launchers.Startup {
        @Inject
        public Startup(final Managers managers, final DefaultDependencyConfig dependencyConfig) {
            super(managers, dependencyConfig);
            getTaskTriggers().convention(Collections.emptyList());
        }
    }

    public abstract static class Probe extends Base implements Launchers.Probe {
        @Inject
        public Probe(final Managers managers, final DefaultDependencyConfig dependencyConfig) {
            super(managers, dependencyConfig);
            getTaskTriggers().convention(Arrays.asList("settings.gradle", "build.gradle", "gradle.properties"));
            getMainTasksArguments().convention(managers.getProject().provider(() -> {
                File mainInit = FileUtil.concat(
                        FileUtil.getLgdDir(managers.getProject()).getAsFile(), Constants.MAIN_INIT_SCRIPT_GRADLE);
                return Arrays.asList("--init-script", mainInit.getAbsolutePath());
            }));
        }
    }

    public abstract static class Build extends Base implements Launchers.Build {
        @Inject
        public Build(final Managers managers, final DefaultDependencyConfig dependencyConfig) {
            super(managers, dependencyConfig);
            getTaskTriggers().convention(Collections.emptyList());
            getMainTasksArguments().convention(managers.getProject().provider(() -> {
                return Arrays.asList(
                        "--init-script",
                        dependencyConfig
                                .getDependencyCallBack()
                                .get()
                                .getGradleInfo()
                                .getInitScript()
                                .getAbsolutePath());
            }));
            getMainTasks().convention(managers.getProject().provider(() -> {
                var tags = dependencyConfig.getDependencyCallBack().get().getBuildTags();
                if (tags.isEmpty()) return Collections.emptyList();
                if (tags.contains(Dependency.Type.MavenLocal)) return Collections.singletonList("publishToMavenLocal");
                return Collections.singletonList("build");
            }));
            getExplicit().convention(managers.getProject().provider(() -> {
                return managers.getDependencyManager()
                                .getSubDepTags(dependencyConfig.getName().get())
                        != null;
            }));
        }
    }

    public abstract static class Base extends GroovyObjectSupport implements Launchers.Base, ConfigFinalizer {
        protected final Property<Boolean> isRunNeeded;

        public Base(final Managers managers, final DefaultDependencyConfig dependencyConfig) {
            getExplicit().convention(false);
            getForwardOutput().convention(true);
            getPreTasksArguments().convention(Collections.emptyList());
            getPreTasks().convention(Collections.emptyList());
            getPostTasksArguments().convention(Collections.emptyList());
            getPostTasks().convention(Collections.emptyList());

            isRunNeeded = managers.getProject().getObjects().property(Boolean.class);
        }

        public Property<Boolean> getIsRunNeeded() {
            return isRunNeeded;
        }

        @Override
        public void finalizeProps() {
            ClassUtil.finalizeProperties(this, Launchers.Base.class);
        }
    }

    private DefaultLaunchers() {}
}
