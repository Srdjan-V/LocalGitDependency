package io.github.srdjanv.localgitdependency.git;

import org.gradle.internal.impldep.org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface GitTasks {
    void setup();
    @Nullable
    Optional<Boolean> hasLocalChanges();
    @Nullable
    Optional<Boolean>  isUpToDateWithRemote();
    void update();
    void clearLocalChanges();
}
