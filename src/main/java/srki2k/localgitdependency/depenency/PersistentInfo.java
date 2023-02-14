package srki2k.localgitdependency.depenency;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import srki2k.localgitdependency.Constants;
import srki2k.localgitdependency.injection.model.imp.DefaultLocalGitDependencyInfoModel;
import srki2k.localgitdependency.property.Property;

import java.io.*;

public class PersistentInfo {
    private final Dependency dependency;
    private final File persistentFile;
    private final GsonSerializableProperty gsonSerializableProperty;
    private boolean validModel;
    private boolean dirty;

    public PersistentInfo(Property dependencyProperty, Dependency dependency) {
        this.dependency = dependency;
        this.persistentFile = Constants.persistentJsonFile.apply(dependencyProperty.getPersistentFolder(), dependency.getName());
        this.gsonSerializableProperty = new GsonSerializableProperty();
        try {
            loadFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Dependency getDependency() {
        return dependency;
    }

    public String getWorkingDirSHA1() {
        return gsonSerializableProperty.workingDirSHA1;
    }

    public void setWorkingDirSHA1(String workingDirSHA1) {
        this.dirty = true;
        this.gsonSerializableProperty.workingDirSHA1 = workingDirSHA1;
    }

    public boolean isValidModel() {
        return validModel;
    }

    public DefaultLocalGitDependencyInfoModel getDefaultLocalGitDependencyInfoModel() {
        return gsonSerializableProperty.projectProbe;
    }

    public void setDefaultLocalGitDependencyInfoModel(DefaultLocalGitDependencyInfoModel defaultLocalGitDependencyInfoModel) {
        this.dirty = true;
        this.validModel = true;
        this.gsonSerializableProperty.projectProbe = defaultLocalGitDependencyInfoModel;
    }

    private void loadFromJson() throws IOException {
        Gson gson = new GsonBuilder().create();
        GsonSerializableProperty persistentProperty = gson.fromJson(new BufferedReader(new FileReader(persistentFile)), GsonSerializableProperty.class);
        if (persistentProperty == null) return;
        this.gsonSerializableProperty.workingDirSHA1 = persistentProperty.workingDirSHA1;
        this.gsonSerializableProperty.projectProbe = persistentProperty.projectProbe;

        if (persistentProperty.projectProbe != null && persistentProperty.projectProbe.versionUID == DefaultLocalGitDependencyInfoModel.serialVersionUID) {
            validModel = true;
        }
    }

    public void saveToPersistentFile() {
        if (!dirty) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (PrintWriter pw = new PrintWriter(persistentFile)) {
            pw.write(gson.toJson(gsonSerializableProperty));
        } catch (FileNotFoundException ignore) {
        }
    }

    public static class GsonSerializableProperty {
        private String workingDirSHA1;
        private DefaultLocalGitDependencyInfoModel projectProbe;
    }

}
