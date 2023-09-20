package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.project.Managers;
import io.github.srdjanv.localgitdependency.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;
import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.internal.Actions;
import org.jetbrains.annotations.NotNull;

public class LGDHelper extends GroovyObjectSupport {
    private final Managers managers;

    public LGDHelper(Managers managers) {
        this.managers = managers;
    }

    /**
     * default maven local publishing
     */
    public Provider<org.gradle.api.artifacts.Dependency> mavenLocal(@NotNull final String notation) {
        return mavenLocal(notation, Actions.doNothing());
    }

    public Provider<org.gradle.api.artifacts.Dependency> mavenLocal(
            @NotNull final String notation, @NotNull final Action<org.gradle.api.artifacts.Dependency> config) {
        return repo(Dependency.Type.MavenLocal, notation, config);
    }

    /**
     * crates a flat dir repository at the build libs of the project
     */
    public Provider<org.gradle.api.artifacts.Dependency> flatDir(@NotNull final String notation) {
        return flatDir(notation, Actions.doNothing());
    }

    public Provider<org.gradle.api.artifacts.Dependency> flatDir(
            @NotNull final String notation, @NotNull final Action<org.gradle.api.artifacts.Dependency> config) {
        return repo(Dependency.Type.JarFlatDir, notation, config);
    }

    /**
     * directly add jar dependencies to the project
     */
    public Provider<ConfigurableFileCollection> jar(@NotNull final String notation) {
        return jar(notation, Actions.doNothing());
    }

    public Provider<ConfigurableFileCollection> jar(
            @NotNull final String notation, @NotNull final Action<ConfigurableFileCollection> config) {
        Objects.requireNonNull(notation);
        Objects.requireNonNull(config);
        managers.getDependencyManager().tagDep(notation, Dependency.Type.Jar);

        return managers.getProject().provider(() -> {
            var files = managers.getProject().getObjects().fileCollection();
            files.setFrom(resolveJars(notation));
            config.execute(files);
            return files;
        });
    }

    private Provider<org.gradle.api.artifacts.Dependency> repo(
            final @NotNull Dependency.Type type,
            final @NotNull String notation,
            final @NotNull Action<org.gradle.api.artifacts.Dependency> config) {
        Objects.requireNonNull(notation);
        Objects.requireNonNull(config);
        managers.getDependencyManager().tagDep(notation, type);

        return managers.getProject().provider(() -> {
            var resolvedNotation = resolveNotation(notation);
            var dep = managers.getProject().getDependencies().create(resolvedNotation);
            config.execute(dep);
            return dep;
        });
    }

    private String resolveNotation(final String notationTarget) {
        final var inputNotation = notationTarget.split(":");
        final var dep = getDependency(inputNotation);

        final var subDep = getSubDependency(inputNotation, dep);
        if (subDep == null) {
            return getDepNotation(inputNotation, dep);
        } else return getSubDepNotation(inputNotation, subDep);
    }

    private Dependency getDependency(final String[] notation) {
        final var name = notation[0].split("\\.")[0];
        return managers.getDependencyManager().getDependencies().stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Dependency with name: %s not found", notation[0])));
    }

    private SubDependencyData getSubDependency(final String[] inputNotation, final Dependency dependency) {
        final String subDepName;
        int firstChar = inputNotation[0].indexOf('.');
        if (firstChar != -1) {
            subDepName = inputNotation[0].substring(firstChar + 1);
        } else return null;

        for (SubDependencyData subDependency :
                dependency.getPersistentInfo().getProbeData().getSubDependencyData()) {
            if (subDependency.getName().equals(subDepName)) return subDependency;
        }
        return null;
    }

    private String getDepNotation(final String[] inputNotation, final Dependency dependency) {
        return getDepNotation(
                dependency.getPersistentInfo().getProbeData().getProjectID().split(":"),
                inputNotation,
                dependency.getPersistentInfo().getProbeData().getArchivesBaseName());
    }

    private String getSubDepNotation(final String[] inputNotation, final SubDependencyData dep) {
        return getDepNotation(dep.getProjectID().split(":"), inputNotation, dep.getArchivesBaseName());
    }

    private String getDepNotation(
            final String[] depNotation, final String[] inputNotation, final String archiveNotation) {
        return switch (inputNotation.length) {
            case 1 -> depNotation[0] + ":" + archiveNotation + ":" + depNotation[2];
            case 2 -> depNotation[0] + ":" + inputNotation[1] + ":" + depNotation[2];
            case 3 -> {
                if (inputNotation[2].split("\\.").length != 1) {
                    yield depNotation[0] + ":" + archiveNotation + ":" + inputNotation[2];
                }
                yield depNotation[0] + ":" + inputNotation[1] + ":" + depNotation[2];
            }
            default -> throw new IllegalStateException("Unexpected value: " + inputNotation.length);
        };
    }

    private List<String> resolveJars(String notation) {
        final var inputNotation = notation.split(":");
        final var dep = getDependency(inputNotation);

        final var subDep = getSubDependency(inputNotation, dep);
        if (subDep == null) {
            return getJars(
                    inputNotation,
                    dep.getGitInfo().getDir(),
                    dep.getPersistentInfo().getProbeData().getArchivesBaseName(),
                    dep.getPersistentInfo().getProbeData().getProjectID());
        } else
            return getJars(
                    inputNotation, new File(subDep.getGitDir()), subDep.getArchivesBaseName(), subDep.getProjectID());
    }

    private List<String> getJars(String[] inputNotation, File libsDir, String archivesBaseName, String projectID) {
        List<String> ret = new ArrayList<>();
        libsDir = FileUtil.toBuildDir(libsDir);

        if (inputNotation.length > 1) {
            var pattern = Pattern.compile(inputNotation[1]);
            for (File file : Objects.requireNonNull(libsDir.listFiles())) {
                if (pattern.matcher(file.getName()).find()) ret.add(file.getAbsolutePath());
            }
            return ret;
        }

        final var fileID = archivesBaseName + "-" + projectID.split(":")[2];
        for (File file : Objects.requireNonNull(libsDir.listFiles())) {
            if (file.getName().contains(fileID)) ret.add(file.getAbsolutePath());
        }
        return ret;
    }
}
