package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.config.impl.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.dependency.DependencyData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.ErrorUtil;
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private DependencyData dependencyData;
    private ProjectProbeData projectProbeData;
    private boolean validDataVersion;
    private boolean dependencyTypeChanged;
    private boolean dirty;

    public PersistentInfo(Managers managers, DependencyConfig dependencyConfig, Dependency dependency, ErrorUtil errorBuilder) {
        this.dependency = dependency;

        if (dependency.getName() != null) {
            File dir;
            if (dependencyConfig.getPersistentDir() != null) {
                dir = dependencyConfig.getPersistentDir();
            } else {
                dir = managers.getPropertyManager().getPluginConfig().getPersistentDir();
            }
            this.persistentFile = Constants.persistentJsonFile.apply(dir,
                    dependency.getName());
        } else this.persistentFile = null;
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

    public void setWorkingDirSHA1(String SHA1) {
        setDirty();
        dependencyData.setWorkingDirSHA1(SHA1);
    }

    @Nullable
    public String getInitFileSHA1() {
        return dependencyData.getInitFileSHA1();
    }

    public void setInitFileSHA1(String SHA1) {
        setDirty();
        dependencyData.setInitFileSHA1(SHA1);
    }

    public boolean isValidDataVersion() {
        return validDataVersion;
    }

    public ProjectProbeData getProbeData() {
        return projectProbeData;
    }

    public void setProbeData(String jsonData) {
        setDirty();
        setValidDataVersion();
        projectProbeData = DataParser.parseJson(jsonData);
    }

    public void setStartupTasksStatus(boolean status) {
        setTaskData(dependencyData::getStartupTasksSuccessful,
                dependencyData::setStartupTasksSuccessful,
                status);
    }

    @Nullable
    public String getStartupTasksTriggersSHA1() {
        return dependencyData.getStartupTasksTriggersSHA1();
    }

    public void setStartupTasksTriggersSHA1(String SHA1) {
        setDirty();
        dependencyData.setStartupTasksTriggersSHA1(SHA1);
    }

    public void setProbeTasksStatus(boolean status) {
        setTaskData(dependencyData::getProbeTasksSuccessful,
                dependencyData::setProbeTasksSuccessful,
                status);
    }

    @Nullable
    public String getProbeTasksTriggersSHA1() {
        return dependencyData.getProbeTasksTriggersSHA1();
    }

    public void setProbeTasksTriggersSHA1(String SHA1) {
        setDirty();
        dependencyData.setProbeTasksTriggersSHA1(SHA1);
    }

    public void setBuildStatus(boolean status) {
        setTaskData(dependencyData::getBuildTasksSuccessful,
                dependencyData::setBuildTasksSuccessful,
                status);
    }

    @Nullable
    public String getBuildTasksTriggersSHA1() {
        return dependencyData.getBuildTasksTriggersSHA1();
    }

    public void setBuildTasksTriggersSHA1(String SHA1) {
        setDirty();
        dependencyData.setBuildTasksTriggersSHA1(SHA1);
    }

    private void setTaskData(Supplier<Boolean> data, Consumer<Boolean> dataSetter, boolean status) {
        if (data.get() != null) {
            if (status != data.get()) {
                setDirty();
                dataSetter.accept(status);
            }
        } else {
            setDirty();
            dataSetter.accept(status);
        }
    }

    public boolean isSuccessfulStartup() {
        return getTaskData(dependencyData::getStartupTasksSuccessful);
    }

    public boolean isSuccessfulProbe() {
        return getTaskData(dependencyData::getProbeTasksSuccessful);
    }

    public boolean isSuccessfulBuild() {
        return getTaskData(dependencyData::getBuildTasksSuccessful);
    }

    private boolean getTaskData(Supplier<Boolean> data) {
        if (data.get() == null) {
            return false;
        } else {
            return data.get();
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

    void setValidDataVersion() {
        validDataVersion = true;
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
