/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package io.kabu.backend.r2c.scripting

import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.withUpdatedClasspath
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = scriptCompilationConfiguration()
    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
}

fun evalFile(scriptString: String, additionalClasspath: String? = null): ResultWithDiagnostics<EvaluationResult> {
    val baseConfig = scriptCompilationConfiguration()
    val compilationConfiguration = when {
        additionalClasspath != null -> baseConfig.withUpdatedClasspath(listOf(File(additionalClasspath)))
        else -> baseConfig
    }
    return BasicJvmScriptingHost().eval(scriptString.toScriptSource(), compilationConfiguration, null)
}

private fun scriptCompilationConfiguration(): ScriptCompilationConfiguration =
    createJvmCompilationConfigurationFromTemplate<SimpleScript> {
        jvm {
            // configure dependencies for compilation, they should contain at least the script base class and
            // its dependencies
            // variant 1: try to extract current classpath and take only a path to the specified "script.jar"
            dependenciesFromCurrentContext(
                "backend", /* script library jar name (exact or without a version) */
                "runtime",
                "annotation",
            )
            // variant 2: try to extract current classpath and use it for the compilation without filtering
//            dependenciesFromCurrentContext(wholeClasspath = true)
            // variant 3: try to extract a classpath from a particular classloader (or Thread.contextClassLoader by default)
            // filtering as in the variant 1 is supported too
//            dependenciesFromClassloader(classLoader = SimpleScript::class.java.classLoader, wholeClasspath = true)
            // variant 4: explicit classpath
//            updateClasspath(listOf(File("/path/to/jar")))
        }
        compilerOptions.append("-language-version=1.8", "-opt-in=kotlin.ExperimentalStdlibApi")
    }

fun main(vararg args: String) {
    if (args.size != 1) {
        println("usage: <app> <script file>")
    } else {
        val scriptFile = File(args[0])
        println("Executing script $scriptFile")

        val res = evalFile(scriptFile)

        res.reports.forEach {
            println(" : ${it.message}" + if (it.exception == null) "" else ": ${it.exception}")
        }
    }
}
