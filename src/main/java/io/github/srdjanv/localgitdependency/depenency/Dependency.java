package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultSourceSetMapper;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.project.Managers;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class Dependency {
    private final String name;
    private final DefaultSourceSetMapper mappers;
    private final boolean ideSupport;
    private final boolean generateGradleTasks;
    private final Set<Type> buildTags;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;
    private final PersistentInfo persistentInfo;

    public Dependency(Managers managers, DependencyConfig dependencyConfig) {
        this.name = dependencyConfig.getName().get();
        var lgdIde = managers.getLGDExtensionByType(LGDIDE.class);
        this.mappers = (DefaultSourceSetMapper) lgdIde.getMappers().findByName(name);
        this.ideSupport = mappers != null
                ? mappers.getRecursive().get()
                : lgdIde.getEnableIdeSupport().get();
        this.generateGradleTasks = dependencyConfig.getGenerateGradleTasks().get();
        this.buildTags =
                Collections.unmodifiableSet(dependencyConfig.getDependecyTags().get());

        this.gitInfo = new GitInfo(managers, dependencyConfig, this);
        this.gradleInfo = new GradleInfo(managers, dependencyConfig, this);
        this.persistentInfo = new PersistentInfo(managers, dependencyConfig, this);
    }

    @NotNull public String getName() {
        return name;
    }

    @Nullable public DefaultSourceSetMapper getSourceSetMapper() {
        return mappers;
    }

    public boolean isIdeSupportEnabled() {
        return ideSupport;
    }

    public boolean isGenerateGradleTasks() {
        return generateGradleTasks;
    }

    @NotNull @Unmodifiable
    public Set<Type> getBuildTags() {
        return buildTags;
    }

    @NotNull public GitInfo getGitInfo() {
        return gitInfo;
    }

    @NotNull public GradleInfo getGradleInfo() {
        return gradleInfo;
    }

    @NotNull public PersistentInfo getPersistentInfo() {
        return persistentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public enum Type {
        MavenLocal,
        JarFlatDir,
        Jar,
        Class
    }
}
