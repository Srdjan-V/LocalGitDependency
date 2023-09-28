package io.github.srdjanv.localgitdependency.dependency;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.srdjanv.localgitdependency.TestConstants;
import io.github.srdjanv.localgitdependency.config.dependency.DependencyConfig;
import io.github.srdjanv.localgitdependency.project.JavaSupplier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class DependencyRegistry {
    private static final List<Entry> registry = new ArrayList<>();

    static {
        registerGradleBranch("4.10");
        registerGradleBranch("5.0");
        registerGradleBranch("6.0");
        registerGradleBranch("7.0");
        registerGradleBranch("8.0");
    }

    private static void registerGradleBranch(final String gradleVersion) {
        final var branchName = getGradleBranch(gradleVersion);
        registry.add(new Entry(branchName, config -> {
            config.getBranch().set(branchName);
            config.getBuildLauncher().getExecutable().set(JavaSupplier.getJava8());
            config.getForceGitUpdate().set(true);
        }));
    }

    public static String getGradleBranch(String version) {
        return "Gradle-" + version;
    }

    @Unmodifiable
    public static Map<Integer, String> getCommitsOfBranch(String version) {
        try {
            List<Map<String, ?>> parsed =
                    new Gson().fromJson(getJson(version), new TypeToken<List<Map<String, ?>>>() {});
            Map<Integer, String> ret = new HashMap<>();
            for (int i = 0; i < parsed.size(); i++)
                ret.put(i, (String) parsed.get(i).get("sha"));

            return Collections.unmodifiableMap(ret);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull private static String getJson(String version) throws IOException {
        var branchName = getGradleBranch(version);
        URL url = new URL(String.format(
                "https://api.github.com/repos/%s/%s/commits?sha=%s",
                TestConstants.GithubOwner, TestConstants.GithubTestProjectName, branchName));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        var json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                json.append(line);
            }
        }
        return json.toString();
    }

    public static List<DependencyWrapper> getAllTestDependencies() {
        return registry.stream().map(DependencyWrapper::new).collect(Collectors.toList());
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
