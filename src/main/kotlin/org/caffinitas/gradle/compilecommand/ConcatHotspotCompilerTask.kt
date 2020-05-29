/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.stream.Stream

@CacheableTask
open class ConcatHotspotCompilerTask : DefaultTask() {
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    var input: File? = null

    @get:OutputFile
    var output: File? = null

    @TaskAction
    fun run() {
        val files = input!!.listFiles()
                ?: throw GradleException("input '$input' is not a directory containing files")

        // Sort the input file names to make the output deterministic and cacheable
        files.sortBy { f -> f.name }
        concatFiles(Arrays.stream(files), output!!)
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