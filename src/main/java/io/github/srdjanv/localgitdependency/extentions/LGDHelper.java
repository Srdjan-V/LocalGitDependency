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
        final var name = getDependencyName(notation);
        managers.getDependencyManager().markBuild(name, type);

        return managers.getProject().provider(() -> {
            var resolvedNotation = resolveNotation(notation);
            var dep = managers.getProject().getDependencies().create(resolvedNotation);
            config.execute(dep);
            return dep;
        });
    }

    private String getDependencyName(final String notation) {
        var args = notation.split(":");
        if (args.length == 0) {
            return notation;
        } else return args[0];
    }

    private String resolveNotation(final String notationTarget) {
        final var inputNotation = notationTarget.split(":");
        final var dep = getDependency(notationTarget, inputNotation);

        final SubDependencyData subDep;
        if (inputNotation.length > 1) {
            subDep = getSubDependency(inputNotation, dep);
        } else subDep = null;

        if (subDep == null) {
            return getDepNotation(inputNotation, dep);
        } else return getSubDepNotation(inputNotation, subDep);
    }

    private Dependency getDependency(final String notation, final String[] args) {
        if (args.length == 0) {
            return managers.getDependencyManager().getDependencies().stream()
                    .filter(d -> d.getName().equals(notation))
                    .findFirst()
                    .orElseThrow(() ->
                            new NoSuchElementException(String.format("Dependency with name: %s not found", notation)));
        } else
            return managers.getDependencyManager().getDependencies().stream()
                    .filter(d -> d.getName().equals(args[0]))
                    .findFirst()
                    .orElseThrow(() ->
                            new NoSuchElementException(String.format("Dependency with name: %s not found", args[0])));
    }

    private SubDependencyData getSubDependency(final String[] inputNotation, final Dependency dependency) {
        for (SubDependencyData subDependency :
                dependency.getPersistentInfo().getProbeData().getSubDependencyData()) {
            var notation = subDependency.getName().split("\\.");
            if (notation.length == 1 && inputNotation[1].equals(notation[0])) return subDependency;

            if (notation.length > inputNotation.length) continue;

            boolean valid = true;
            for (int i = 0; i < notation.length; i++) {
                if (!notation[i].equals(inputNotation[i])) valid = false;
            }

            if (valid) return subDependency;
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
                if (inputNotation[1].split("\\.").length != 0) {
                    yield depNotation[0] + ":" + archiveNotation + ":" + inputNotation[1];
                }
                yield depNotation[0] + ":" + inputNotation[1] + ":" + depNotation[2];
            }
            default -> throw new IllegalStateException("Unexpected value: " + inputNotation.length);
        };
    }
}
