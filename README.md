Gradle plugin with support for incremental compilation for the [compile-command-annotations](http://compile-command-annotations.nicoulaj.net/) project 

Writes a single output file suitable to be loaded via the `-XX:CompileCommandFile=<file>` JVM option.

Usage w/ Java projects:

```(kotlin)
plugins {
    id("org.caffinitas.gradle.compilecommand") version "0.1"
}

compileCommands {
    add {
        outputFile = project.file("conf/hotspot_compiler")
        sourceSet = sourceSets.main
    }
}
```

The plugin adds the dependency for the compile-command annotation-processor to the `compileOnly` and
`annotationProcessor` configurations. The default value for that dependency is
`net.nicoulaj.compile-command-annotations:compile-command-annotations:1.2.3` and can be changed via the
property `compileCommands.compileCommandsDependency`.

See https://github.com/nicoulaj/compile-command-annotations
See http://compile-command-annotations.nicoulaj.net/
