package io.github.srdjanv.localgitdependency.config.dependency;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public final class Launchers {
    private Launchers() {
    }

    public interface Startup extends Base {
    }

    public interface Probe extends Base {
    }

    public interface Build extends Base {
    }

    public interface Base {
        /**
         * Sets the tasks should explicitly run
         */
        Property<Boolean> explicit();

        Property<String> forwardOutput(Boolean forwardOutput);

        /**
         * This will set what files should trigger a launcher rerun
         * <p>
         * Currently, this checked by String::Contains
         */
        ListProperty<String> getTaskTriggers();

        ListProperty<String> getPreTasksArguments();

        ListProperty<String> getPreTasks();

        ListProperty<String> getMainTasksArguments();

        ListProperty<String> getMainTasks();

        ListProperty<String> getPostTasksArguments();

        ListProperty<String> getPostTasks();
    }
}
