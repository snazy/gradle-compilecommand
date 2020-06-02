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

import org.gradle.api.Action
import java.util.*

@Suppress("unused")
open class CompileCommandsExtension {
    var compileCommandsDependency: Any = "net.nicoulaj.compile-command-annotations:compile-command-annotations:1.2.3"
    private var compileCommands: MutableList<CompileCommand> = ArrayList()
    fun getCompileCommands(): List<CompileCommand> {
        return compileCommands
    }

    fun setCompileCommands(compileCommands: MutableList<CompileCommand>) {
        this.compileCommands = compileCommands
    }

    fun add(configurer: Action<CompileCommand>) {
        val compileCommand = CompileCommand()
        configurer.execute(compileCommand)
        compileCommands.add(compileCommand)
    }
}
