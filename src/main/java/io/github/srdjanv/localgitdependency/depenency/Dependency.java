package io.github.srdjanv.localgitdependency.depenency;

import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.config.dependency.SourceSetMapper;
import io.github.srdjanv.localgitdependency.config.dependency.impl.DefaultDependencyConfig;
import io.github.srdjanv.localgitdependency.extentions.LGDIDE;
import io.github.srdjanv.localgitdependency.git.GitInfo;
import io.github.srdjanv.localgitdependency.gradle.GradleInfo;
import io.github.srdjanv.localgitdependency.persistence.PersistentInfo;
import io.github.srdjanv.localgitdependency.project.Managers;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class Dependency {
    private final String name;
    private final List<SourceSetMapper.Mapping> mappings;
    private final boolean ideSupport;
    private final boolean generateGradleTasks;
    private final Set<Type> buildTags;
    private final GitInfo gitInfo;
    private final GradleInfo gradleInfo;
    private final PersistentInfo persistentInfo;

    public Dependency(Managers managers, DependencyConfig dependencyConfig) {
        ((DefaultDependencyConfig) dependencyConfig)
                .getDependencyCallBack()
                .value(this)
                .finalizeValue();

        this.name = dependencyConfig.getName().get();
        if (name.contains(".")) throw new IllegalArgumentException("Illegal character '.' in dependency name");
        var lgdIde = managers.getLGDExtensionByType(LGDIDE.class);
        var mapper = lgdIde.getMappers().findByName(name);
        if (mapper != null) {
            this.mappings =
                    Collections.unmodifiableList(mapper.getMappings().stream().collect(Collectors.toList()));
            this.ideSupport = mapper.getRecursive().get();
        } else {
            this.mappings = Collections.emptyList();
            this.ideSupport = lgdIde.getEnableIdeSupport().get();
        }
        this.generateGradleTasks = dependencyConfig.getGenerateGradleTasks().get();
        this.buildTags =
                Collections.unmodifiableSet(dependencyConfig.getDependencyTags().get());

        this.gitInfo = new GitInfo(managers, dependencyConfig, this);
        this.gradleInfo = new GradleInfo(managers, dependencyConfig, this);
        this.persistentInfo = new PersistentInfo(managers, dependencyConfig, this);
    }

    @NotNull public String getName() {
        return name;
    }

    @Unmodifiable
    public List<SourceSetMapper.Mapping> getSourceSetMappings() {
        return mappings;
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
