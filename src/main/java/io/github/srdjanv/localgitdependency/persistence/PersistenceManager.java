package io.github.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;
import org.gradle.api.GradleException;

import java.io.*;

// TODO: 28/03/2023 implement interface access and move all serialization logic to this class
class PersistenceManager extends ManagerBase implements IPersistenceManager {
    private PersistentProjectData serializableProperty;
    private boolean dirty;

    PersistenceManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {
        File initScriptFolder = getPropertyManager().getGlobalProperty().getPersistentDir();
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

    @Override
    public String getInitScriptSHA() {
        return serializableProperty.mainInitSha1;
    }

    @Override
    public void setInitScriptSHA(String initScriptSHA) {
        this.serializableProperty.mainInitSha1 = initScriptSHA;
        this.dirty = true;
    }

    @Override
    public void savePersistentData() {
        if (dirty) {
            File initScriptFolder = getPropertyManager().getGlobalProperty().getPersistentDir();
            File mainInitJson = Constants.concatFile.apply(initScriptFolder, Constants.PROJECT_DATA_JSON);

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
}
