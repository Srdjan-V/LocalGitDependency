package io.github.srdjanv.localgitdependency.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.ProjectInstances;

import java.io.*;
import java.util.Objects;

class PersistenceManager extends ManagerBase implements IPersistenceManager {
    private PersistentProjectData serializableProperty;
    private File projectDataJson;
    private boolean dirty;
    private Gson gson;

    PersistenceManager(ProjectInstances projectInstances) {
        super(projectInstances);
    }

    @Override
    protected void managerConstructor() {
        serializableProperty = new PersistentProjectData();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String getInitScriptSHA() {
        return serializableProperty.getMainInitSha1();
    }

    @Override
    public void setInitScriptSHA(String initScriptSHA) {
        serializableProperty.setMainInitSha1(initScriptSHA);
        dirty = true;
    }

    @Override
    public void loadPersistentData() {
        loadProjectPersistentData();
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            loadDependencyPersistentData(dependency);
        }
    }

    @Override
    public void savePersistentData() {
        saveProjectPersistentData();
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            saveDependencyPersistentData(dependency);
        }
    }

    @Override
    public void loadProjectPersistentData() {
        File initScriptFolder = getPropertyManager().getGlobalProperty().getPersistentDir();
        projectDataJson = Constants.concatFile.apply(initScriptFolder, Constants.PROJECT_DATA_JSON);

        if (projectDataJson.exists()) {
            if (projectDataJson.isDirectory()) {
                throw new RuntimeException(Constants.PROJECT_DATA_JSON + " at " + projectDataJson.getAbsolutePath() + " can not be a directory");
            }
            PersistentProjectData serializableProperty = readJson(projectDataJson, PersistentProjectData.class);
            if (serializableProperty == null) return;
            this.serializableProperty.setMainInitSha1(serializableProperty.getMainInitSha1());
        }
    }

    @Override
    public void saveProjectPersistentData() {
        if (dirty) {
            writeJson(projectDataJson, serializableProperty);
            dirty = false;
        }
    }

    @Override
    public void saveDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        if (persistentInfo.isDirty()) {
            writeJson(persistentInfo.getPersistentFile(), persistentInfo.getPersistentDependencyData());
            dirty = false;
        }
    }

    @Override
    public void loadDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        if (!persistentInfo.getPersistentFile().exists()) return;
        PersistentDependencyData persistentProperty = readJson(persistentInfo.getPersistentFile(), PersistentDependencyData.class);
        if (persistentProperty == null) return;
        PersistentDependencyData persistentDependencyData = persistentInfo.getPersistentDependencyData();
        persistentDependencyData.setWorkingDirSHA1(persistentProperty.getWorkingDirSHA1());
        persistentDependencyData.setProjectProbe(persistentProperty.getProjectProbe());
        persistentDependencyData.setInitFileSHA1(persistentProperty.getInitFileSHA1());

        if (persistentProperty.getProjectProbe() != null && Objects.equals(persistentProperty.getProjectProbe().version, Constants.PROJECT_VERSION)) {
            persistentInfo.setValidModel();
        }

        if (persistentProperty.getDependencyType() != dependency.getDependencyType()) {
            persistentDependencyData.setDependencyType(dependency.getDependencyType());
            persistentInfo.setDependencyTypeChanged();
            persistentInfo.setDirty();
        }
    }

    private <T> T readJson(File file, Class<T> clazz) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return gson.fromJson((reader), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeJson(File file, Object dataObject) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.write(gson.toJson(dataObject));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
