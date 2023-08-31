package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
import io.github.srdjanv.localgitdependency.persistence.data.probe.subdeps.SubDependencyData;
import io.github.srdjanv.localgitdependency.project.Managers;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.gradle.api.Action;
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

    /*    //directly add jar dependencies to the project
    public Provider<FileCollection> jar(@NotNull final String notation) {
        getDependencyManager().markBuild(getDependencyName(notation), io.github.srdjanv.localgitdependency.depenency.Dependency.Type.Jar);
    }

    public Provider<FileCollection> task(String name) {

    }*/

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
        return managers.getDependencyManager().getDependencies().stream()
                .filter(d -> d.getName().equals(notation[0]))
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
            if (subDependency.getName().equals(subDepName))
                return subDependency;
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
        return getDepNotation(dep.getArchivesBaseName().split("\\."), inputNotation, dep.getArchivesBaseName());
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
}
