package com.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.project.ManagerBase;
import com.srdjanv.localgitdependency.project.ProjectBuilder;
import org.gradle.api.GradleException;

import java.io.*;

public class PersistenceManager extends ManagerBase {
    private final SerializableProperty serializableProperty;
    private boolean dirty;

    public PersistenceManager(ProjectBuilder projectBuilder) {
        super(projectBuilder);
        File initScriptFolder = getPropertyManager().getGlobalProperty().getPersistentFolder();
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
            File initScriptFolder = getPropertyManager().getGlobalProperty().getPersistentFolder();
            File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_JSON);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(mainInitJson)) {
                pw.write(gson.toJson(serializableProperty));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }

        for (Dependency dependency : getDependencyManager().getDependencies()) {
            dependency.getPersistentInfo().saveToPersistentFile();
        }
    }

    private static class SerializableProperty {
       private String mainInitSha1;
    }
}
