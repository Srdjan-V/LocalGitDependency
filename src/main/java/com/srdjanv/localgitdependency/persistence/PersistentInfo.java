package com.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import com.srdjanv.localgitdependency.injection.model.imp.DefaultLocalGitDependencyInfoModel;
import com.srdjanv.localgitdependency.property.Property;

import java.io.*;

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

    public void setInitFileSHA1SHA1(String initFileSHA1) {
        this.dirty = true;
        this.serializableProperty.initFileSHA1 = initFileSHA1;
    }

    public boolean isValidModel() {
        return validModel;
    }

    public SerializableProperty.DependencyInfoModelSerializable getDefaultLocalGitDependencyInfoModel() {
        return serializableProperty.projectProbe;
    }

    public void setDefaultLocalGitDependencyInfoModel(LocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel) {
        this.dirty = true;
        this.validModel = true;
        this.serializableProperty.projectProbe = new SerializableProperty.DependencyInfoModelSerializable(defaultLocalGitDependencyInfoModel);
    }

    private void loadFromJson() throws IOException {
        Gson gson = new GsonBuilder().create();
        SerializableProperty persistentProperty = gson.fromJson(new BufferedReader(new FileReader(persistentFile)), SerializableProperty.class);
        if (persistentProperty == null) return;
        this.serializableProperty.workingDirSHA1 = persistentProperty.workingDirSHA1;
        this.serializableProperty.projectProbe = persistentProperty.projectProbe;

        if (persistentProperty.projectProbe != null && persistentProperty.projectProbe.versionUID == DefaultLocalGitDependencyInfoModel.serialVersionUID) {
            validModel = true;
        }

        if (persistentProperty.dependencyType != this.getDependency().getDependencyType()) {
            dependencyTypeChanged = true;
        }
    }

    public void saveToPersistentFile() {
        if (dirty) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(persistentFile)) {
                pw.write(gson.toJson(serializableProperty));
            } catch (FileNotFoundException ignore) {
            }
            dirty = false;
        }
    }
}
