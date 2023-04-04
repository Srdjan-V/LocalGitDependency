package io.github.srdjanv.localgitdependency.persistence.data.dependency;

import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.NonNullData;

public class DependencyData implements DependencyDataGetter, DependencyDataSetter, NonNullData {
    private String workingDirSHA1;
    private String initFileSHA1;
    private Dependency.Type dependencyType;

    @Override
    public String getWorkingDirSHA1() {
        return workingDirSHA1;
    }

    @Override
    public void setWorkingDirSHA1(String workingDirSHA1) {
        this.workingDirSHA1 = workingDirSHA1;
    }

    @Override
    public String getInitFileSHA1() {
        return initFileSHA1;
    }

    @Override
    public void setInitFileSHA1(String initFileSHA1) {
        this.initFileSHA1 = initFileSHA1;
    }

    @Override
    public Dependency.Type getDependencyType() {
        return dependencyType;
    }

    @Override
    public void setDependencyType(Dependency.Type dependencyType) {
        this.dependencyType = dependencyType;
    }
}