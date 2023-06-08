package io.github.srdjanv.localgitdependency.persistence;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.DataLayout;
import io.github.srdjanv.localgitdependency.persistence.data.DataParser;
import io.github.srdjanv.localgitdependency.persistence.data.DataWrapper;
import io.github.srdjanv.localgitdependency.persistence.data.dependency.DependencyData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;
import io.github.srdjanv.localgitdependency.persistence.data.project.ProjectData;
import io.github.srdjanv.localgitdependency.project.ManagerBase;
import io.github.srdjanv.localgitdependency.project.Managers;
import org.gradle.api.GradleException;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class PersistenceManager extends ManagerBase implements IPersistenceManager {
    private ProjectData projectData;
    private File projectDataJson;
    private boolean dirty;

    PersistenceManager(Managers managers) {
        super(managers);
    }

    @Override
    protected void managerConstructor() {
    }

    @Override
    public String getInitScriptSHA() {
        return projectData.getMainInitSHA1();
    }

    @Override
    public void setInitScriptSHA(String initScriptSHA) {
        projectData.setMainInitSHA1(initScriptSHA);
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
        File initScriptFolder = getConfigManager().getPluginConfig().getPersistentDir();
        projectDataJson = Constants.concatFile.apply(initScriptFolder, Constants.PROJECT_DATA_JSON);

        if (projectDataJson.exists()) {
            if (projectDataJson.isDirectory()) {
                throw new GradleException(Constants.PROJECT_DATA_JSON + " at " + projectDataJson.getAbsolutePath() + " can not be a directory");
            }
            projectData = DataParser.simpleLoadDataFromFileJson(projectDataJson, ProjectData.class, ProjectData::new);
        } else {
            projectData = new ProjectData();
        }
    }

    @Override
    public void saveProjectPersistentData() {
        if (dirty) {
            DataParser.simpleSaveDataToFileJson(projectDataJson, projectData);
            dirty = false;
        }
    }

    @Override
    public void saveDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        if (persistentInfo.isDirty()) {
            List<?> data = Arrays.asList(persistentInfo.getDependencyData(), persistentInfo.getProbeData());
            DataParser.complexSaveDataToFileJson(persistentInfo.getPersistentFile(), data, DataLayout.getDependencyLayout());
            dirty = false;
        }
    }

    @Override
    public void loadDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        List<DataWrapper> dataList = DataParser.complexLoadDataFromFileJson(persistentInfo.getPersistentFile(), DataLayout.getDependencyLayout());
        for (DataWrapper dataWrapper : dataList) {
            switch (dataWrapper.getDataType()) {
                case DependencyData: {
                    DependencyData data = (DependencyData) dataWrapper.getData();
                    persistentInfo.setDependencyData(data);
                    if (data.getDependencyType() != dependency.getDependencyType()) {
                        data.setDependencyType(dependency.getDependencyType());
                        persistentInfo.setDependencyTypeChanged();
                        persistentInfo.setDirty();
                    }
                    break;
                }
                case ProjectProbeData: {
                    ProjectProbeData probeData = (ProjectProbeData) dataWrapper.getData();
                    persistentInfo.setProjectProbeData(probeData);
                    if (dataWrapper.isValid() && Objects.equals(probeData.getVersion(), Constants.PROJECT_VERSION)) {
                        persistentInfo.setValidDataVersion();
                    }
                    break;
                }

                default:
                    throw new IllegalStateException();
            }
        }

    }

}
