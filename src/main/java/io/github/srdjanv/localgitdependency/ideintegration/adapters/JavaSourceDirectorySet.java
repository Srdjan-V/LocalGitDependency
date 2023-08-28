package io.github.srdjanv.localgitdependency.ideintegration.adapters;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

class JavaSourceDirectorySet implements Adapter.SourceDirectorySet {
    @Override
    public Adapter.Types getType() {
        return Adapter.Types.Java;
    }

    @Override
    public void configureSource(SourceSet sourceSet, SourceSetData sourceSetData, Project project) {
        sourceSet.java(conf -> {
            conf.setSrcDirs(sourceSetData.getSources());
            conf.getDestinationDirectory().set(project.file(sourceSetData.getBuildClassesDir()));
        });
    }
}
