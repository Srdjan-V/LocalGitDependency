package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.property.impl.DependencyProperty;
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private final PersistentDependencyData persistentDependencyData;
    private boolean validModel;
    private boolean dependencyTypeChanged;
    private boolean dirty;

    public PersistentInfo(DependencyProperty dependencyDependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyDependencyProperty.getPersistentDir(), dependency.getName());
        this.persistentDependencyData = new PersistentDependencyData();
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
        return persistentDependencyData.getWorkingDirSHA1();
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        setDirty();
        persistentDependencyData.setWorkingDirSHA1(workingDirSHA1);
    }

    @Nullable
    public String getInitFileSHA1() {
        return persistentDependencyData.getInitFileSHA1();
    }

    public void setInitFileSHA1(String initFileSHA1) {
        setDirty();
        persistentDependencyData.setInitFileSHA1(initFileSHA1);
    }

    public boolean isValidModel() {
        return validModel;
    }

    public PersistentDependencyData.DependencyInfoModelSerializable getProbeData() {
        return persistentDependencyData.getProjectProbe();
    }

    public void setProbeData(LocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel) {
        setDirty();
        setValidModel();
        persistentDependencyData.setProjectProbe(new PersistentDependencyData.DependencyInfoModelSerializable(defaultLocalGitDependencyInfoModel));
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

    PersistentDependencyData getPersistentDependencyData() {
        return persistentDependencyData;
    }

    boolean isDirty() {
        return dirty;
    }
}
