package io.github.srdjanv.localgitdependency.extentions;

import groovy.lang.GroovyObjectSupport;
import io.github.srdjanv.localgitdependency.depenency.Dependency;
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
            var resolvedNotation = resolveNotation(type, notation);
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

    private String resolveNotation(final Dependency.Type type, final String notationTarget) {
        final var inputNotation = notationTarget.split(":");
        final var dep = getDependency(notationTarget, inputNotation);
        final var depNotation =
                dep.getPersistentInfo().getProbeData().getProjectID().split(":");

        if (inputNotation.length >= 4) { // full sub dep notation
            return getSubDepNotation(depNotation, inputNotation, dep, type);
        }

        if (inputNotation.length == 3) { // full dep notation
            return getDepNotation(depNotation, inputNotation, dep, type);
        }

        if (inputNotation.length == 2) {
            if (inputNotation[1].equals(dep.getPersistentInfo().getProbeData().getArchivesBaseName())
                    || inputNotation[1].equals(dep.getName())) {
                return getDepNotation(depNotation, inputNotation, dep, type);
            }
            return getSubDepNotation(depNotation, inputNotation, dep, type);
        }

        return getDepNotation(depNotation, inputNotation, dep, type);
    }

    private Dependency getDependency(final String notation, final String[] args) {
        if (args.length == 0) {
            return managers.getDependencyManager().getDependencies().stream()
                    .filter(d -> d.getName().equals(notation))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No value present"));
        } else
            return managers.getDependencyManager().getDependencies().stream()
                    .filter(d -> d.getName().equals(args[0]))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    private String getDepNotation(
            final String[] depNotation, final String[] inputNotation, Dependency dependency, Dependency.Type type) {
        final String archiveNotation =
                dependency.getPersistentInfo().getProbeData().getArchivesBaseName();
        /*        switch (type) {
            case MavenLocal, JarFlatDir ->
                    archiveNotation = dependency.getPersistentInfo().getProbeData().getArchivesBaseName();
            case MavenProjectLocal, MavenProjectDependencyLocal -> archiveNotation = dependency.getName();
            default -> throw new IllegalStateException();
        }*/

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

    // TODO: 24/08/2023
    private String getSubDepNotation(
            final String[] depNotation, final String[] inputNotation, Dependency dependency, Dependency.Type type) {
        final String archiveNotation =
                dependency.getPersistentInfo().getProbeData().getArchivesBaseName();
        /*        switch (type) {
            case MavenLocal, JarFlatDir ->
                    archiveNotation = dependency.getPersistentInfo().getProbeData().getArchivesBaseName();
            case MavenProjectLocal, MavenProjectDependencyLocal -> archiveNotation = dependency.getName();
            default -> throw new IllegalStateException();
        }*/

        return switch (inputNotation.length) {
            case 1 -> depNotation[0] + archiveNotation + depNotation[2];
            case 2 -> depNotation[0] + inputNotation[1] + depNotation[2];
            case 3 -> {
                if (inputNotation[1].split("\\.").length != 0) {
                    yield depNotation[0] + archiveNotation + inputNotation[1];
                }
                yield depNotation[0] + inputNotation[1] + depNotation[2];
            }
            default -> throw new IllegalStateException("Unexpected value: " + inputNotation.length);
        };
    }
}
