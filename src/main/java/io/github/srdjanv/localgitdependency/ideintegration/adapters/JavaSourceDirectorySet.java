package io.github.srdjanv.localgitdependency.ideintegration.adapters;

import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.SourceSetData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.sourcesetdata.directoryset.DirectorySetData;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

class JavaSourceDirectorySet implements Adapter.SourceDirectorySet {
    @Override
    public Adapter.Types getType() {
        return Adapter.Types.Java;
    }

    @Override
    public void configure(SourceSet sourceSet, DirectorySetData directorySetData, Project project) {
        sourceSet.java(conf -> {
            conf.setSrcDirs(directorySetData.getSources());
            conf.getDestinationDirectory().set(project.file(directorySetData.getBuildClassesDir()));
        });
    }
}
