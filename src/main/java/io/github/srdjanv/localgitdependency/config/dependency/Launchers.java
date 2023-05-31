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
        void explicit(Boolean explicit);

        void setTaskTriggers(String... files);

        void addTaskTriggers(String... files);

        void preTasks(String... tasks);

        void mainTasks(String... tasks);

        void postTasks(String... tasks);
    }
}
