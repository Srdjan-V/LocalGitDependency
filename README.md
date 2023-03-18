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
in the inner Builder class
of [DefaultProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/DefaultProperty.java).
With the global configuration you can also configure default dependency properties(They will get overwritten by the dependency configuration),
thous properties can be found in this
class [CommonProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/CommonPropertyBuilder.java),
they get shared with the dependency.

The dependency properties can be found in the inner Builder class
of [Property](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/Property.java#L28), and in
[CommonProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/CommonPropertyBuilder.java)

I will not provide a descriptions for every existing property,
since everything you see can change and some parts probably will.

You can also specify how the build dependency will be added to the
project, [available dependency types](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/depenency/Dependency.java#L137)

The plugin also has default properties, they are located in this
instance [globalProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/io/github/srdjanv/localgitdependency/property/PropertyManager.java)

### Limitations  ###

Projects that are using a different java version may or may not build,
you can supply the correct java version by using the `javaHomeDir` property for the dependency

### Examples  ###

In the projects `build.gradle` file add the following:

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

You can change the directories that the plugin uses, the paths can be absolute or relative

```
localGitDependency {
    configureGlobal {
        gitDir "./yourGitDir"
        persistentDir new File("./yourPersistentDir")
        mavenDir "/rootMaven"
    }
}
```

You can now also add dependencies like so

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

While using the jar dependency type you can use `generatedJarsToAdd` to filter what jars are going to be added as dependencies

```
localGitDependency {
    implementation "https://example.com/repository.git", {
        generatedJarsToAdd(["1.1.0.jar", "1.1.0-sources.jar"])
    }
}
```

`generatedArtifactNames` is the same as `generatedJarsToAdd` but for configuring repositories. See the java doc for more information 

```
localGitDependency {
    implementation "https://example.com/repository.git", {
        generatedArtifactNames(["name", "group:name:version"])
    }
}
```

It's possible to add a dependency configuration that will be used by gradle's DependencyHandler 

```
localGitDependency {
    implementation "https://example.com/repository.git", {
        configure {
            transitive = false
        }
    }
}
```

If you don't want to configure your dependencies is such a way or don't need for the dependency to be registered

```
dependencies {
    implementation "example.com:repository:1.0.1"
}
localGitDependency {
    implementation 'https://example.com/repository.git', {
        registerDependencyToProject = false
    }
}
```

You can change for how long gradle daemons will idle by using `gradleDaemonMaxIdleTime`, this is in seconds

```
localGitDependency {
    implementation 'https://example.com/repository.git', {
        gradleDaemonMaxIdleTime = 60
    }
}
```