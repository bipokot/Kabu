package io.kabu.frontend.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspIncrementalLog
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.kabu.backend.common.log.InterceptingLogging
import org.intellij.lang.annotations.Language
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

open class BaseKspFrontendProcessorTest : KotlinCompilationResultAssert() {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var tempDirPath: Path

    @Before
    fun before() {
        tempDirPath = createTempDir()
    }

    @After
    fun after() {
        removeTempDir(tempDirPath)
    }

    private fun createTempDir(): Path {
        val file = File("build/ksptesting")
        if (file.exists() && !file.deleteRecursively()) error("ERROR: temp dir deletion failed: ${file.absolutePath}")
        file.mkdirs()
        return file.toPath()
    }

    private fun removeTempDir(path: Path) {
        val file: File = path.toFile()
        if (!file.deleteRecursively()) error("ERROR: temp dir deletion failed: ${path.toAbsolutePath()}")
    }

    protected fun compileAndCheck(
        @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
        block: KotlinCompilation.Result.() -> Unit,
    ) {
        val kotlinSources = createKotlinFiles(code)
        val compilationResult = compile(kspWithCompilation = false, kotlinSources + emptyJavaSourceFile())
        compilationResult.block()
    }

    private fun compile(
        kspWithCompilation: Boolean,
        sourceFiles: List<SourceFile>,
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            verbose = false
            sources = sourceFiles
            symbolProcessorProviders = listOf(KspFrontendProcessorProvider())
            workingDir = tempDirPath.toFile().also { logger.debug(it.absolutePath) }
            inheritClassPath = true
            kspIncremental = false
            kspArgs = kabuKspOptions
            kspIncrementalLog = false
            this.kspWithCompilation = kspWithCompilation
        }.compile()
    }

    protected fun compileAndCheckAndRun(
        @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
        vararg cases: TestCase,
        onCompilationResult: KotlinCompilation.Result.() -> Unit = { assertOk() },
    ) {
        val kotlinSources = createKotlinFiles(code)
        val compilationResult = compile(kspWithCompilation = true, kotlinSources + emptyJavaSourceFile())
        compilationResult.onCompilationResult()
        cases.forEach {
            validateCase(compilationResult, it)
        }
    }

    private fun validateCase(compilationResult: KotlinCompilation.Result, testCase: TestCase) {
        val actualOutput = getScriptOutput(testCase.sampleScript)
        when (val expected = testCase.expectedResult) {
            is TestCase.ScriptResult.Termination -> assertEquals(expected.string, actualOutput)
            is TestCase.ScriptResult.Error.KspError -> compilationResult.assertKspError(expected.message)
            is TestCase.ScriptResult.Error.Exception -> TODO()
        }
    }

    private fun getScriptOutput(sampleScript: String): String {
        logger.debug { "Running script for '$sampleScript'" }

        val (packageName, contents) = groupByPackageNames(sampleScript).entries.single()
        val fileContents = getKotlinTestFilePrefix(packageName) + contents + KOTLIN_TEST_FILE_SUFFIX

        val file = File.createTempFile("sample", ".main.kts").apply {
            writeText(fileContents)
        }
        val process = ProcessBuilder("kotlin", "-cp", createClassPath(), file.absolutePath).start()
        return if (process.waitFor() != 0) {
            val errOutput = process.errorStream.bufferedReader().readText()
            System.err.println(errOutput)
            ""
        } else {
            val processOutput = process.inputStream.bufferedReader().readText()
            System.out.println(processOutput)
            processOutput
        }
    }

    private fun createClassPath(): String {
        val projectRoot = "../../../"
        val mainKtsLib = "${projectRoot}frontend/ksp/testing/lib/kotlin-main-kts-1.9.0.jar"
        val runtimeClassFiles = "${projectRoot}runtime/build/classes/kotlin/main"
        val annotationClassFiles = "${projectRoot}annotation/build/classes/kotlin/main"
        val classFiles = "${projectRoot}frontend/ksp/processor/build/ksptesting/classes"
        return "$mainKtsLib:$annotationClassFiles:$runtimeClassFiles:$classFiles"
    }

    private companion object {
        val logger = InterceptingLogging.logger {}

        val kabuKspOptions = mutableMapOf("ksp.io.kabu.allowUnsafe" to "true")
    }
}
