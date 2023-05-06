package io.github.srdjanv.localgitdependency.persistence.data.dependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface DependencyDataGetter {
    String getWorkingDirSHA1();
    String getInitFileSHA1();
    Dependency.Type getDependencyType();
    Boolean getBuildSuccessful();
}
