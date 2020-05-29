Gradle plugin with support for incremental compilation for the [compile-command-annotations](http://compile-command-annotations.nicoulaj.net/) project 

Writes a single output file suitable to be loaded via the `-XX:CompileCommandFile=<file>` JVM option.

Usage:

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

See https://github.com/nicoulaj/compile-command-annotations
See http://compile-command-annotations.nicoulaj.net/
