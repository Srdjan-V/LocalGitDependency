package io.github.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import io.github.srdjanv.localgitdependency.property.Property;

import java.io.*;
import java.util.Objects;

public class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private final SerializableProperty serializableProperty;
    private boolean validModel;
    private boolean dependencyTypeChanged;
    private boolean dirty;

    public PersistentInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyProperty.getPersistentFolder(), dependency.getName());
        this.serializableProperty = new SerializableProperty();
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
        return serializableProperty.workingDirSHA1;
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        this.dirty = true;
        this.serializableProperty.workingDirSHA1 = workingDirSHA1;
    }

    public String getInitFileSHA1() {
        return serializableProperty.initFileSHA1;
    }

    public void setInitFileSHA1(String initFileSHA1) {
        this.dirty = true;
        this.serializableProperty.initFileSHA1 = initFileSHA1;
    }

    public boolean isValidModel() {
        return validModel;
    }

    public SerializableProperty.DependencyInfoModelSerializable getProbeData() {
        return serializableProperty.projectProbe;
    }

    public void setProbeData(LocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel) {
        this.dirty = true;
        this.validModel = true;
        this.serializableProperty.projectProbe = new SerializableProperty.DependencyInfoModelSerializable(defaultLocalGitDependencyInfoModel);
    }

    private void loadFromJson() throws IOException {
        Gson gson = new GsonBuilder().create();
        if (!persistentFile.exists()) return;
        SerializableProperty persistentProperty = gson.fromJson(new BufferedReader(new FileReader(persistentFile)), SerializableProperty.class);
        if (persistentProperty == null) return;
        this.serializableProperty.workingDirSHA1 = persistentProperty.workingDirSHA1;
        this.serializableProperty.projectProbe = persistentProperty.projectProbe;
        this.serializableProperty.initFileSHA1 = persistentProperty.initFileSHA1;

        if (persistentProperty.projectProbe != null && Objects.equals(persistentProperty.projectProbe.version, Constants.PROJECT_VERSION)) {
            validModel = true;
        }

        if (persistentProperty.dependencyType != this.getDependency().getDependencyType()) {
            serializableProperty.dependencyType = this.getDependency().getDependencyType();
            dependencyTypeChanged = true;
            dirty = true;
        }
    }

    public void saveToPersistentFile() {
        if (dirty) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(persistentFile)) {
                pw.write(gson.toJson(serializableProperty));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }
    }
}
