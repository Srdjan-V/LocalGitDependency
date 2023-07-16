LocalGitDependency
=====================

Gradle plugin to build external git repositories and add them as dependencies.
*The main focus of this plugin are java project, other types may or may not work*

*Minimum gradle version for dependency projects 4.10.0*

**Note this plugin is still actively developed, braking changes might get introduced.**

### Setup ###

You can add this plugin to your top-level build script using the following configuration:

### `plugins` block:

```groovy
plugins {
    id "io.github.srdjanv.local-git-dependency" version "$version"
}
```

or via the

### `buildscript` block:

```groovy

buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath "io.github.srdjanv.local-git-dependency:$version"
    }
}

apply plugin: "io.github.srdjanv.local-git-dependency"
```

### Technical Explanation  ###

The [PluginBuilder](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/config/plugin/PluginBuilder.java)
is used to configure basic plugin behaviour

The [DefaultableBuilder](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/config/dependency/defaultable/DefaultableBuilder.java)
is used to configure the defaults of all registered dependencies

It is possible to specify how the build dependency will be added to the
project,
see [Dependency.Types](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/depenency/Dependency.java#L137)

The default properties are located in
the [ConfigManager](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/iogithub/srdjanv/localgitdependency/config/ConfigManager.java)

### Plugin Configuration ###

You can change the directories that the plugin uses, the paths can be absolute or relative.
Changing global paths requires you to manually enable or disable the cleanup-manager,
the manager will delete anything under thous directories that doesn't mach the registered dependencies

```
localGitDependency {
    configurePlugin {
        automaticCleanup false
        gitDir "./yourGitDir"
        persistentDir new File("./yourPersistentDir")
        mavenDir "/rootMaven"
    }
}
```

### Dependency Configuration ###

### Basic ###

With this way the ide can tell to what object the closure will
delegate

```
localGitDependency {
    add("https://example.com/repository.git")

    add("https://example.com/repository.git", {
        name "test"
    })

    add(implementation, "https://example.com/repository.git")

    add(implementation "https://example.com/repository.git", {
        name "test"
    })
}
```

You can use the dynamic add method to registrar dependencies, this only allows for shorter declaration.

```
localGitDependency {
    "https://example.com/repository.git"

    "https://example.com/repository.git" {
        name "test"
    }

    implementation "https://example.com/repository.git"

    implementation "https://example.com/repository.git", {
        name "test"
    }
}
```

The plugin can try to generate source or javadoc jars for the dependency

```
localGitDependency {
    configureDefaultable {
        tryGeneratingSourceJar = true
        tryGeneratingJavaDocJar = true
    }
    
    add("https://example.com/repository.git", {
        //this is overwritten the global configuration
        tryGeneratingJavaDocJar = false
    })
}
```

### Complex ###

#### Artifact Handling ####

You are able to fine tune what generated artifacts are going to be used, and how they are going to get configured

```
localGitDependency {
    add("https://example.com/repository.git", {
        configuration({
            configuration "runtimeOnly"
            closure ({
                transitive false
            })
            include "notation", "someOtherNotation"
            include ["mapNotation": {
                transitive true
            }]
        }, {
            configuration "someOtherConfiguration"
            exclude "notation", "someOtherNotation"
        })
    })
}
```

#### Gradle Interaction ####

The build process in decided into several stages, startup, probe and build. Each one has its one pre, main and post
tasks and arguments

```
localGitDependency {
    add('https://example.com/repository.git', {
        buildLauncher {
            startup {
                preTaksWithArguments "CustomArgs"
                preTaks "SomeTask"
            }
            probe {
                mainTaksWithArguments "CustomArgs"
                mainTaks "SomeTask"
            }
            probe {
                postTasksWithArguments "CustomArgs"
                postTasks "SomeTask"
            }
        }
    })
}
```

Each stages run condition can be configured individually
```
localGitDependency {
    add('https://example.com/repository.git', {
        buildLauncher {
            startup {
                setTaskTriggers ".gitignore"
                addTaskTriggers "gradle.properties"
            }
            build {
                explicit true// this stage will now always run. its used to add custom condition with your build file
            }
        }
    })
}
```


### MultiProject IDE integration ###

To enable this you need to set `enableIdeSupport` to true.
Ideally the generated jars should be added to a runtimeOnly configuration, and source sets should be mapped

```
localGitDependency {
    add('https://example.com/repository.git', {
        configuration "runtimeOnly"
        enableIdeSupport = true
        mapSourceSets({
            map "main//The main source set of the cuurent project", "main//The main source set of the dep", "someOtherSourceSet//Some other source set of the dep"
        }, {
            map "test//The test source set of the cuurent project", "someOtherSourceSet"
        })
    })
}
```
