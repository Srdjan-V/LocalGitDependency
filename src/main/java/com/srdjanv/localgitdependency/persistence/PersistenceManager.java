package com.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.srdjanv.localgitdependency.Constants;
import com.srdjanv.localgitdependency.Instances;
import com.srdjanv.localgitdependency.depenency.Dependency;
import org.gradle.api.GradleException;

import java.io.*;

public class PersistenceManager {
    private String initScriptSHA;
    private boolean dirty;

    public PersistenceManager() {
        File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
        File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_JSON);
        if (mainInitJson.exists()) {
            if (mainInitJson.isDirectory()) {
                throw new GradleException(Constants.MAIN_INIT_SCRIPT_JSON + " at " + mainInitJson.getAbsolutePath() + " can not be a directory");
            }
            try {
                Gson gson = new GsonBuilder().create();
                initScriptSHA = gson.fromJson(new BufferedReader(new FileReader(mainInitJson)), String.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getInitScriptSHA() {
        return initScriptSHA;
    }

    public void setInitScriptSHA(String initScriptSHA) {
        this.initScriptSHA = initScriptSHA;
        this.dirty = true;
    }

    public void savePersistentData() {
        if (dirty) {
            File initScriptFolder = Instances.getPropertyManager().getGlobalProperty().getPersistentFolder();
            File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.MAIN_INIT_SCRIPT_JSON);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (PrintWriter pw = new PrintWriter(mainInitJson)) {
                pw.write(gson.toJson(initScriptSHA));
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
