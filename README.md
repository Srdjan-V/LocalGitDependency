LocalGitDependency
=====================

Gradle plugin to build external git repositories and add them as dependencies.

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

The configuration part of this plugin is decided into 2 parts, the global configuration, and the dependency
configuration.

The global configuration constants properties that will configure some aspects of the plugin, thous aspect can be found
in [GlobalBuilder class](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/GlobalBuilder.java)
.
With the global configuration you can also configure default dependency properties(They will get overwritten by the
dependency configuration),
thous properties can be found
in [CommonBuilder class](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/GlobalBuilder.java)
,
they get shared with the dependency.

The dependency properties can be found in the
[DependencyBuilder class](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/DependencyBuilder.java)
, and in the
[CommonBuilder class](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/GlobalBuilder.java)

Javadoc is included that will explain every property

You can also specify how the build dependency will be added to the
project, [available dependency types](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/depenency/Dependency.java#L137)

The default properties are located in
the [PropertyManager cass](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/PropertyManager.java)

### Limitations  ###

Projects that are using a different java version may or may not build,
you can supply the correct java version by using the `javaHomeDir` property for the dependency

### Examples  ###

In the projects `build.gradle` file add the following:

You can use the add method to registrar dependencies, with this way the ide can tell to what object the closure will
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

```
localGitDependency {
    add("https://example.com/repository.git",{
        name 'DependencyName'
        //only use one, the last one will be used if you specifly multiple 
        commit '1234fg'
        tag 'v1.0.0'
        branch 'dev'
    })
}
```

By default, the plugin will use java's default configuration, `implementation`. If you want you can specify it yourself

```
localGitDependency {
    configureGlobal {
        configuration 'customGlobalConfiguration'
    }
    
    add(`configuration`, "https://example.com/repository.git",{
        name 'DependencyName'
        commit "1234fg"
    })
    
    add("https://example.com/repository.git",{
        configuration 'customConfiguration'
    })
}
```

The plugin can try to generate source or javadoc jars for the dependency

```
localGitDependency {
    configureGlobal {
        tryGeneratingSourceJar = true
        tryGeneratingJavaDocJar = true
    }
    
    add("https://example.com/repository.git", {
        //this is overwritten the global configuration
        tryGeneratingJavaDocJar = false
    })
}
```

You can change the directories that the plugin uses, the paths can be absolute or relative.
Changing global paths requires you to manually enable or disable the cleanup-manager,
the manager will delete anything under thous directories that doesn't mach the registered dependencies

```
localGitDependency {
    configureGlobal {
        automaticCleanup false
        gitDir "./yourGitDir"
        persistentDir new File("./yourPersistentDir")
        mavenDir "/rootMaven"
    }
}
```

While using the jar dependency type you can use `generatedJarsToAdd` to filter what jars are going to be added as
dependencies

```
localGitDependency {
    add("https://example.com/repository.git", {
        generatedJarsToAdd(["1.1.0.jar", "1.1.0-sources.jar"])
    })
}
```

`generatedArtifactNames` is the same as `generatedJarsToAdd` but for configuring repositories. See the java doc for more
information

```
localGitDependency {
    add("https://example.com/repository.git", {
        generatedArtifactNames(["name", "group:name:version"])
    })
}
```

It's possible to add a dependency configuration that will be used by gradle's DependencyHandler

```
localGitDependency {
    add("https://example.com/repository.git", {
        configure {
            transitive = false
        }
    })
}
```

You can specify if the dependency should be registered, setting this to false will also disable ide support

```
localGitDependency {
    add('https://example.com/repository.git', {
        registerDependencyToProject = false
    })
}
```

By enabling this the plugin will register the source sets, configurations, repositories and its dependencies to your project.
Currently, repositories are not being added automatically and that's why it's disabled by default
```
localGitDependency {
    add('https://example.com/repository.git', {
        enableIdeSupport = true
    })
}
```

You can change for how long gradle daemons will idle by using `gradleDaemonMaxIdleTime`, this is in seconds

```
localGitDependency {
    add('https://example.com/repository.git', {
        gradleDaemonMaxIdleTime = 60
    })
}
```