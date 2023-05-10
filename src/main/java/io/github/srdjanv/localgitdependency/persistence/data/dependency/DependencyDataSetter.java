package io.github.srdjanv.localgitdependency.persistence.data.dependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;

public interface DependencyDataSetter {
    void setWorkingDirSHA1(String workingDirSHA1);
    void setInitFileSHA1(String initFileSHA1);
    void setDependencyType(Dependency.Type dependencyType);
    void setStartupTasksRun(boolean startupTasksRun);
    void setBuildSuccessful(boolean buildSuccessful);
}
