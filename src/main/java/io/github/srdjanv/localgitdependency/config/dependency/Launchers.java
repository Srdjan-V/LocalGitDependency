package io.github.srdjanv.localgitdependency.config.dependency;

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
        void explicit(Boolean explicit);

        /**
         * This will set what files should trigger a launcher rerun
         * <p>
         * Currently, this checked by String::Contains
         */
        void setTaskTriggers(String... files);

        /**
         * Same as above, but it adds to the default existing ones
         */
        void addTaskTriggers(String... files);

        void preTasksWithArguments(String... args);

        void preTasks(String... tasks);

        void mainTasksWithArguments(String... args);

        void mainTasks(String... tasks);

        void postTasksWithArguments(String... args);

        void postTasks(String... tasks);

        void forwardOutput(Boolean forwardOutput);
    }
}
