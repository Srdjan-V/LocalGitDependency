package io.github.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.property.impl.Property;

import java.io.*;
import java.util.Objects;

public class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private final PersistentDependencyData persistentDependencyData;
    private boolean validModel;
    private boolean dependencyTypeChanged;
    private boolean dirty;

    public PersistentInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyProperty.getPersistentDir(), dependency.getName());
        this.persistentDependencyData = new PersistentDependencyData();
        try {
            loadFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasDependencyTypeChanged() {
        return dependencyTypeChanged;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public String getWorkingDirSHA1() {
        return persistentDependencyData.workingDirSHA1;
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        this.dirty = true;
        this.persistentDependencyData.workingDirSHA1 = workingDirSHA1;
    }

    public String getInitFileSHA1() {
        return persistentDependencyData.initFileSHA1;
    }

    public void setInitFileSHA1(String initFileSHA1) {
        this.dirty = true;
        this.persistentDependencyData.initFileSHA1 = initFileSHA1;
    }

    public boolean isValidModel() {
        return validModel;
    }

    public PersistentDependencyData.DependencyInfoModelSerializable getProbeData() {
        return persistentDependencyData.projectProbe;
    }

    public void setProbeData(LocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel) {
        this.dirty = true;
        this.validModel = true;
        this.persistentDependencyData.projectProbe = new PersistentDependencyData.DependencyInfoModelSerializable(defaultLocalGitDependencyInfoModel);
    }

    private void loadFromJson() throws IOException {
        Gson gson = new GsonBuilder().create();
        if (!persistentFile.exists()) return;
        PersistentDependencyData persistentProperty = gson.fromJson(new BufferedReader(new FileReader(persistentFile)), PersistentDependencyData.class);
        if (persistentProperty == null) return;
        this.persistentDependencyData.workingDirSHA1 = persistentProperty.workingDirSHA1;
        this.persistentDependencyData.projectProbe = persistentProperty.projectProbe;
        this.persistentDependencyData.initFileSHA1 = persistentProperty.initFileSHA1;

        if (persistentProperty.projectProbe != null && Objects.equals(persistentProperty.projectProbe.version, Constants.PROJECT_VERSION)) {
            validModel = true;
        }

        if (persistentProperty.dependencyType != this.getDependency().getDependencyType()) {
            persistentDependencyData.dependencyType = this.getDependency().getDependencyType();
            dependencyTypeChanged = true;
            dirty = true;
        }
    }

    public void saveToPersistentFile() {
        if (dirty) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(persistentFile)) {
                pw.write(gson.toJson(persistentDependencyData));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }
    }
}
