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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class PersistenceManager extends ManagerBase implements IPersistenceManager {
    private ProjectData projectData;
    private File projectDataJson;
    private boolean probeDataUpdateNeeded;
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
    public boolean savePersistentData() {
        boolean didWork = saveProjectPersistentData();
        for (Dependency dependency : getDependencyManager().getDependencies()) {
            didWork |= saveDependencyPersistentData(dependency);
        }
        return didWork;
    }

    @Override
    public void loadProjectPersistentData() {
        projectDataJson = Constants.concatFile.apply(Constants.lgdDir.apply(getProject()).getAsFile(), Constants.PROJECT_DATA_JSON);

        if (projectDataJson.exists()) {
            if (projectDataJson.isDirectory()) {
                throw new GradleException(Constants.PROJECT_DATA_JSON + " at " + projectDataJson.getAbsolutePath() + " can not be a directory");
            }
            projectData = DataParser.simpleLoadDataFromFileJson(projectDataJson, ProjectData.class, ProjectData::new);
            if (!Objects.equals(projectData.getPluginVersion(), Constants.PLUGIN_VERSION)) {
                projectData.setPluginVersion(Constants.PLUGIN_VERSION);
                probeDataUpdateNeeded = true;
            }
        } else {
            projectData = new ProjectData();
            projectData.setPluginVersion(Constants.PLUGIN_VERSION);
            probeDataUpdateNeeded = true;
        }
    }

    @Override
    public boolean saveProjectPersistentData() {
        if (dirty) {
            DataParser.simpleSaveDataToFileJson(projectDataJson, projectData);
            dirty = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean saveDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        if (persistentInfo.isDirty()) {
            List<?> data = Arrays.asList(persistentInfo.getDependencyData(), persistentInfo.getProbeData());
            DataParser.complexSaveDataToFileJson(persistentInfo.getPersistentFile(), data, DataLayout.getDependencyLayout());
            dirty = false;
            return true;
        }
        return false;
    }

    @Override
    public void loadDependencyPersistentData(Dependency dependency) {
        PersistentInfo persistentInfo = dependency.getPersistentInfo();

        List<DataWrapper> dataList = DataParser.complexLoadDataFromFileJson(persistentInfo.getPersistentFile(), DataLayout.getDependencyLayout());
        for (DataWrapper dataWrapper : dataList) {
            switch (dataWrapper.getDataType()) {
                case DependencyData -> {
                    DependencyData data = (DependencyData) dataWrapper.getData();
                    persistentInfo.setDependencyData(data);

                    if (data.getBuildTypes() == null || !dependency.getBuildTargets().containsAll(data.getBuildTypes())) {
                        data.setBuildTypes(dependency.getBuildTargets());
                        persistentInfo.setDependencyTypeChanged();
                        persistentInfo.setDirty();
                    }
                }
                case ProjectProbeData -> {
                    ProjectProbeData probeData = (ProjectProbeData) dataWrapper.getData();
                    persistentInfo.setProjectProbeData(probeData);
                    if (dataWrapper.isValid() && !probeDataUpdateNeeded) {
                        persistentInfo.setValidDataVersion();
                    }
                }
                default -> throw new IllegalStateException();
            }
        }

    }

}
