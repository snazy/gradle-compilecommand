/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.caffinitas.gradle.compilecommand

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.stream.Stream

/**
 * Contains all the boilerplate code to use `compile-command-annotations` with Gradle incremental
 * Java compilation.
 */
@Suppress("unused")
class CompileCommandPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        extensions.create<CompileCommandsExtension>("compileCommands")

        afterEvaluate {
            val compileCommands = extensions.getByType<CompileCommandsExtension>()
            for (compileCommand in compileCommands.getCompileCommands()) {
                val sourceSet = compileCommand.resolveSourceSet(this)

                dependencies.add(sourceSet!!.annotationProcessorConfigurationName, compileCommands.compileCommandsDependency)
                dependencies.add(sourceSet.compileOnlyConfigurationName, compileCommands.compileCommandsDependency)

                tasks.named<JavaCompile>(sourceSet.compileJavaTaskName) {
                    options.compilerArgs.add("-Acompile.command.incremental.output=" + compileCommand.intermediateFilesDirectory)

                    val outputDir = sourceSet.java.outputDir.resolve(compileCommand.intermediateFilesDirectory)

                    doLast {
                        val files = outputDir.listFiles()
                                ?: throw GradleException("input '$outputDir' is not a directory containing files")

                        // Sort the input file names to make the output deterministic and cacheable
                        files.sortBy { f -> f.name }
                        concatFiles(Arrays.stream(files), compileCommand.outputFile!!)
                    }
                }
            }
        }
    }

    private fun concatFiles(files: Stream<File>, output: File) {
        try {
            FileChannel.open(output.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { w ->
                files.forEach { file: File ->
                    try {
                        FileChannel.open(file.toPath(), StandardOpenOption.READ).use { r ->
                            var remain = r.size()
                            var p = 0L
                            while (remain > 0L) {
                                val tr = r.transferTo(p, remain, w)
                                remain -= tr
                                p += tr
                            }
                        }
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            }
        } catch (e: IOException) {
            throw GradleException("Failed to concatenate files into file $output", e)
        }
    }
}
