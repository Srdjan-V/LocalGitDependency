package io.github.srdjanv.localgitdependency.git;

import java.util.Optional;

public interface IGitTasks {
    void setup();

    Optional<Boolean> hasLocalChanges();

    Optional<Boolean> isUpToDateWithRemote();

    void update();

    void clearLocalChanges();
}
