package io.github.srdjanv.localgitdependency.tasks.printtasks;

import groovy.lang.Closure;
import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.logger.ManagerLogger;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;

import java.lang.reflect.Field;

public interface BasePrintInfoTask {
    default void printInfo(Dependency dependency) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DependencyInfo:").append(System.lineSeparator());
        for (Field field : Dependency.class.getDeclaredFields()) {
            field.setAccessible(true);

            if (filter(field.getType(),
                    GitInfo.class, GradleInfo.class,
                    PersistentInfo.class, Closure.class)) continue;

            Object fieldVal = field.get(dependency);
            stringBuilder.append(Constants.TAB_INDENT);
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        stringBuilder.append("GitInfo:").append(System.lineSeparator());
        for (Field field : GitInfo.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (filter(field.getType(), Dependency.class)) continue;
            Object fieldVal = field.get(dependency.getGitInfo());
            stringBuilder.append(Constants.TAB_INDENT);
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        stringBuilder.append("GradleInfo:").append(System.lineSeparator());
        for (Field field : GradleInfo.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (filter(field.getType(), Dependency.class)) continue;
            Object fieldVal = field.get(dependency.getGradleInfo());
            stringBuilder.append(Constants.TAB_INDENT);
            stringBuilder.append(field.getName()).append(": ").append(fieldVal).append(System.lineSeparator());
        }

        ManagerLogger.infoUnFormatted(stringBuilder.toString());
    }

    static boolean filter(Class<?> type, Class<?>... filter) {
        for (Class<?> aClass : filter) {
            if (type == aClass) return true;
        }

        return false;
    }

}
