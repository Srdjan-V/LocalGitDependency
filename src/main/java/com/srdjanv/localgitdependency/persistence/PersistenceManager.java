package com.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.GradleException;

import java.io.*;

public class PersistenceManager {
    private final SerializableProperty serializableProperty;
    private boolean dirty;

    public PersistenceManager() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
        File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_JSON);
        serializableProperty = new SerializableProperty();
        if (mainInitJson.exists()) {
            if (mainInitJson.isDirectory()) {
                throw new GradleException(Constants.MAIN_INIT_SCRIPT_JSON + " at " + mainInitJson.getAbsolutePath() + " can not be a directory");
            }
            try {
                Gson gson = new GsonBuilder().create();
                SerializableProperty serializableProperty = gson.fromJson(new BufferedReader(new FileReader(mainInitJson)), SerializableProperty.class);
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
            File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_JSON);

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

    private static class SerializableProperty {
       private String mainInitSha1;
    }
}
