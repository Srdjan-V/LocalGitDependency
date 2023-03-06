package io.github.srdjanv.localgitdependency.tasks.printtasks;

import io.github.srdjanv.localgitdependency.Logger;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;

import java.lang.reflect.Field;

public interface BasePrintInfoTask {
    default void printInfo(Dependency dependency) throws IllegalAccessException {
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
