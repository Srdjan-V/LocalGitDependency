package com.srdjanv.localgitdependency.tasks;

import com.srdjanv.localgitdependency.Logger;
import com.srdjanv.localgitdependency.depenency.Dependency;
import com.srdjanv.localgitdependency.git.GitInfo;
import com.srdjanv.localgitdependency.gradle.GradleInfo;
import com.srdjanv.localgitdependency.persistence.PersistentInfo;
import org.gradle.api.tasks.TaskAction;

import java.lang.reflect.Field;

public abstract class PrintDependencyInfo extends BaseDependencyTask {

    @TaskAction
    public void task$PrintDependencyInfo() throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DependencyInfo:").append(System.lineSeparator());
        for (Field field : Dependency.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldVal = field.get(dependency);
            if (fieldVal instanceof GitInfo || fieldVal instanceof GradleInfo || fieldVal instanceof PersistentInfo) continue;
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        stringBuilder.append("GitInfo:").append(System.lineSeparator());
        for (Field field : GitInfo.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldVal = field.get(dependency.getGitInfo());
            if (fieldVal instanceof Dependency) continue;
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        stringBuilder.append("GradleInfo:").append(System.lineSeparator());
        for (Field field : GradleInfo.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldVal = field.get(dependency.getGradleInfo());
            if (fieldVal instanceof Dependency) continue;
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        Logger.info(stringBuilder.toString());
    }

}
