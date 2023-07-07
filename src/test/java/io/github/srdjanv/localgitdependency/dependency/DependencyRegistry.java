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
                "https://github.com/Srdjan-V/TweakedLib.git",
                "47396e09d9469edbdf8666d68781f830f81a7641"));
        //gradle 4.10
        registry.add(new DependencyRegistry(
                "GroovyScriptFG2",
                "https://github.com/CleanroomMC/GroovyScript.git",
                "8390e4a2bd8b599856445eef224823164fff0a85",
                "setupDecompWorkspace"));
    }

    public static List<DependencyWrapper> getTestDependencies() {
        return registry.stream().map(DependencyWrapper::new).collect(Collectors.toList());
    }

    final String dependencyName;
    final String gitUrl;
    final String gitRev;
    final String[] startupTasks;

    private DependencyRegistry(String dependencyName, String gitUrl, String gitRev, String... startupTasks) {
        this.dependencyName = dependencyName;
        this.gitUrl = gitUrl;
        this.gitRev = gitRev;
        this.startupTasks = startupTasks;
    }

}
