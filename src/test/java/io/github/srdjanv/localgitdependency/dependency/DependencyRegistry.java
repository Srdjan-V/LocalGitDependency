package io.github.srdjanv.localgitdependency.dependency;

import com.github.bsideup.jabel.Desugar;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.gradle.api.Action;

public class DependencyRegistry {
    private static final List<Entry> registry = new ArrayList<>();

    public enum Types {
        BRANCH("Branch");

        private final String type;

        Types(String type) {
            this.type = type;
        }

        public String nameType(String baseName) {
            return type + "$" + baseName;
        }

        public String identifier() {
            return type;
        }
    }

    static {
        registerGradleBranch("4.10");
        registerGradleBranch("5.0");
        registerGradleBranch("6.0");
        registerGradleBranch("7.0");
        registerGradleBranch("8.0");
    }

    private static void registerGradleBranch(final String gradleVersion) {
        final var branchName = getGradleBranch(gradleVersion);
        registry.add(new Entry(Types.BRANCH.nameType(branchName), config -> {
            config.getBranch().set(branchName);
        }));
    }

    public static String getGradleBranch(String version) {
        return "Gradle-" + version;
    }

    public static List<DependencyWrapper> getTestDependencies(Predicate<String> filter) {
        return registry.stream()
                .filter(dep -> filter.test(dep.name()))
                .map(DependencyWrapper::new)
                .collect(Collectors.toList());
    }

    public static DependencyWrapper getTestDependency(Predicate<String> filter) {
        return registry.stream()
                .filter(dep -> filter.test(dep.name()))
                .map(DependencyWrapper::new)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Desugar
    public record Entry(String name, Action<DependencyConfig> configAction) {}
}
