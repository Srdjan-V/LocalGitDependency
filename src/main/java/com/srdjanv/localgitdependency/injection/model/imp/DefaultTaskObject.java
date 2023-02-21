package com.srdjanv.localgitdependency.injection.model.imp;

import com.srdjanv.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import com.srdjanv.localgitdependency.injection.model.TaskObject;

import java.io.Serializable;

public class DefaultTaskObject implements TaskObject, Serializable {
    public static long serialVersionUID = LocalGitDependencyInfoModel.serialVersionUID;
    private final String name;
    private final String classifier;

    public DefaultTaskObject(String name, String classifier) {
        this.name = name;
        this.classifier = classifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }
}
