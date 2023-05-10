package io.github.srdjanv.localgitdependency.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyRegistry {
    private static final List<DependencyRegistry> registry = new ArrayList<>();

    static {
        //gradle 7.5
        registry.add(new DependencyRegistry(
                "TweakedLib",
                "https://github.com/Srdjan-V/TweakedLib.git"));
        //gradle 4.10
        registry.add(new DependencyRegistry(
                "GroovyScriptFG2",
                "https://github.com/CleanroomMC/GroovyScript.git",
                "setupDecompWorkspace"));
    }

    public static List<DependencyWrapper> getTestDependencies() {
        return registry.stream().map(DependencyWrapper::new).collect(Collectors.toList());
    }

    final String dependencyName;
    final String gitUrl;
    final String[] startupTasks;

    private DependencyRegistry(String dependencyName, String gitUrl, String... startupTasks) {
        this.dependencyName = dependencyName;
        this.gitUrl = gitUrl;
        this.startupTasks = startupTasks;
    }

}
