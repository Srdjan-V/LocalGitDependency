package io.github.srdjanv.localgitdependency.persistence.data.project;

import io.github.srdjanv.localgitdependency.util.annotations.NonNullData;

@NonNullData
public class ProjectData implements ProjectDataGetters, ProjectDataSetter {
    private String mainInitSHA1;
    private String pluginVersion;

    public ProjectData() {}

    @Override
    public String getMainInitSHA1() {
        return mainInitSHA1;
    }

    @Override
    public void setMainInitSHA1(String mainInitSHA1) {
        this.mainInitSHA1 = mainInitSHA1;
    }

    @Override
    public String getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
}
