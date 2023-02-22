LocalGitDependency
=====================

Gradle plugin to build external git repositories and add them as dependencies.

### Setup ###

You can add this plugin to your top-level build script using the following configuration:

### `plugins` block:


or via the


```groovy
plugins {
  id "com.srdjanv.local-git-dependency" version "$version"
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
      classpath "com.srdjanv.local-git-dependency:$version"
   }
}

apply plugin: "com.srdjanv.local-git-dependency"
```

### Usage ###

**Note this plugin is still actively developed, braking changes might get introduced.**

In the projects `build.gradle` file add the following:

```
localGitDependency {
    add("https://example.com/repository.git",{
        name 'DependencyName'
        commit "1234fg"
    })
}
```

By default, this will use java's default configuration, `implementation`. If you want you can specify it yourself

```
localGitDependency {
    add(`implementation`, "https://example.com/repository.git",{
        name 'DependencyName'
        commit "1234fg"
    })
}
```

Or add it as a part of the configuration closure

```
localGitDependency {
    add("https://example.com/repository.git",{
        defaultConfiguration 'customConfiguration'
    })
}
```

You can also use globalConfigurations to set everything to a default value

```
localGitDependency {
    configureGlobal {
    defaultConfiguration 'customConfiguration'
    }
    
    add("https://example.com/repository.git")
}
```

#### Supported parameters ####

I will not provide a descriptions for every existing parameter,
since everything you see can change and some parts probably will.
But I will explain every possible category

[CommonProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/master/src/main/java/com/srdjanv/localgitdependency/property/CommonPropertyBuilder.java)
Every dependency has thous properties as does the global configuration block

[DefaultProperty](https://github.com/Srdjan-V/LocalGitDependency/blob/0fb6b6c449dc4ef0da43084c1c14132e1106e88f/src/main/java/com/srdjanv/localgitdependency/property/DefaultProperty.java#L20)
Only the global configuration block can have thous properties

[Property](https://github.com/Srdjan-V/LocalGitDependency/blob/0fb6b6c449dc4ef0da43084c1c14132e1106e88f/src/main/java/com/srdjanv/localgitdependency/property/Property.java#L28)
Only dependencies can have thous properties

You can also specify how the build dependency will be added to the project, [available dependency types](https://github.com/Srdjan-V/LocalGitDependency/blob/0fb6b6c449dc4ef0da43084c1c14132e1106e88f/src/main/java/com/srdjanv/localgitdependency/depenency/Dependency.java#L137)

### How it works ###

1. You're providing git repository URL and other optional details in `build.gradle` file.
2. The plugin clones the repository to the default directory `libs/[name]`
   at a specified commit, tag or branch.
3. The cloned repo will be probed for information, for example what java version its using,
   if it has a Maven Publish plugin, and other information.
4. After That an init script will be generated based on the information gathered.
5. Then the cloned project will be build and added as a dependency to the main project
