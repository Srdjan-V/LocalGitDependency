package io.github.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.Instances;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.GradleException;

import java.io.*;

public class PersistenceManager {
    private final PersistentProjectData serializableProperty;
    private boolean dirty;

    public PersistenceManager() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
        File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.PROJECT_DATA_JSON);
        serializableProperty = new PersistentProjectData();
        if (mainInitJson.exists()) {
            if (mainInitJson.isDirectory()) {
                throw new GradleException(Constants.PROJECT_DATA_JSON + " at " + mainInitJson.getAbsolutePath() + " can not be a directory");
            }
            try {
                Gson gson = new GsonBuilder().create();
                PersistentProjectData serializableProperty = gson.fromJson(new BufferedReader(new FileReader(mainInitJson)), PersistentProjectData.class);
                if (serializableProperty == null) return;
                this.serializableProperty.mainInitSha1 = serializableProperty.mainInitSha1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getInitScriptSHA() {
        return serializableProperty.mainInitSha1;
    }

    public void setInitScriptSHA(String initScriptSHA) {
        this.serializableProperty.mainInitSha1 = initScriptSHA;
        this.dirty = true;
    }

    public void savePersistentData() {
        if (dirty) {
            File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
            File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.PROJECT_DATA_JSON);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(mainInitJson)) {
                pw.write(gson.toJson(serializableProperty));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }

        for (Dependency dependency : Instances.getDependencyManager().getDependencies()) {
            dependency.getPersistentInfo().saveToPersistentFile();
        }
    }
}
