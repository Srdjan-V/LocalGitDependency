package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.dependency.DependencyData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public final class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private DependencyData dependencyData;
    private ProjectProbeData projectProbeData;
    private boolean validModel;
    private boolean dependencyTypeChanged;
    private boolean dirty;

    public PersistentInfo(DependencyConfig dependencyConfig, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyConfig.getPersistentDir(), dependency.getName());
    }

    public boolean hasDependencyTypeChanged() {
        return dependencyTypeChanged;
    }

    @NotNull
    public File getPersistentFile() {
        return persistentFile;
    }

    @NotNull
    public Dependency getDependency() {
        return dependency;
    }

    @Nullable
    public String getWorkingDirSHA1() {
        return dependencyData.getWorkingDirSHA1();
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        setDirty();
        dependencyData.setWorkingDirSHA1(workingDirSHA1);
    }

    @Nullable
    public String getInitFileSHA1() {
        return dependencyData.getInitFileSHA1();
    }

    public void setInitFileSHA1(String initFileSHA1) {
        setDirty();
        dependencyData.setInitFileSHA1(initFileSHA1);
    }

    public boolean isValidModel() {
        return validModel;
    }

    public ProjectProbeData getProbeData() {
        return projectProbeData;
    }

    public void setProbeData(String jsonData) {
        setDirty();
        setValidModel();
        projectProbeData = DataParser.parseJson(jsonData);
    }

    public void setBuildStatus(boolean status) {
        if (dependencyData.getBuildSuccessful() != null) {
            if (status != dependencyData.getBuildSuccessful()) {
                setDirty();
                dependencyData.setBuildSuccessful(status);
            }
        } else {
            setDirty();
            dependencyData.setBuildSuccessful(status);
        }
    }

    public void setStartupTasksStatus(boolean status) {
        if (dependencyData.getStartupTasksRun() != null) {
            if (status != dependencyData.getStartupTasksRun()) {
                setDirty();
                dependencyData.setStartupTasksRun(status);
            }
        } else {
            setDirty();
            dependencyData.setStartupTasksRun(status);
        }
    }

    public boolean getBuildStatus() {
        if (dependencyData.getBuildSuccessful() == null) {
            return false;
        } else {
            return dependencyData.getBuildSuccessful();
        }
    }

    public boolean getRunStatus() {
        if (dependencyData.getStartupTasksRun() == null) {
            return false;
        } else {
            return dependencyData.getStartupTasksRun();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentInfo gitInfo = (PersistentInfo) o;
        return Objects.equals(gitInfo.getDependency().getName(), getDependency().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency.getName());
    }

    void setDependencyData(DependencyData dependencyData) {
        this.dependencyData = dependencyData;
    }

    void setProjectProbeData(ProjectProbeData projectProbeData) {
        this.projectProbeData = projectProbeData;
    }

    void setValidModel() {
        validModel = true;
    }

    void setDependencyTypeChanged() {
        dependencyTypeChanged = true;
    }

    void setDirty() {
        dirty = true;
    }

    DependencyData getDependencyData() {
        return dependencyData;
    }

    boolean isDirty() {
        return dirty;
    }
}
