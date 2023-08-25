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

        /**
         * This will set what files should trigger a launcher rerun
         * <p>
         * Currently, this checked by String::Contains
         */
        ListProperty<String> setTaskTriggers();

        /**
         * Same as above, but it adds to the default existing ones
         */
        ListProperty<String> addTaskTriggers();

        ListProperty<String> preTasksWithArguments();

        ListProperty<String> preTasks();

        ListProperty<String> mainTasksWithArguments();

        ListProperty<String> mainTasks();

        ListProperty<String> postTasksWithArguments();

        ListProperty<String> postTasks();

        Property<String> forwardOutput(Boolean forwardOutput);
    }
}
