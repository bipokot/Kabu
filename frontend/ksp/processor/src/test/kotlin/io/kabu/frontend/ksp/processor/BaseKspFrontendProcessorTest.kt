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
import kotlin.test.assertTrue

open class BaseKspFrontendProcessorTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    lateinit var tempDirPath: Path

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
        val kotlinSource = createKotlinFile(code)
        val compilationResult = compile(kspWithCompilation = false, kotlinSource, emptyJavaSourceFile())
        compilationResult.block()
    }

    protected fun KotlinCompilation.Result.assertCompilationError(lineNumber: Int, vararg expectedMessages: String) {
        assertCompilationError()
        assertExpectedLineNumber(lineNumber)

        expectedMessages.forEach {
            assertExpectedMessage(it)
        }
    }

    private fun compile(
        kspWithCompilation: Boolean,
        vararg source: SourceFile,
    ): KotlinCompilation.Result = KotlinCompilation().apply {
        verbose = false
        sources = source.toList()
        symbolProcessorProviders = listOf(KspFrontendProcessorProvider())
//        workingDir = temporaryFolder.root.also { println(it) }
        workingDir = tempDirPath.toFile().also { logger.debug(it.absolutePath) }
        inheritClassPath = true
        kspIncremental = false
        kspArgs = mutableMapOf("ksp.io.kabu.allowUnsafe" to "true")
        kspIncrementalLog = false
        this.kspWithCompilation = kspWithCompilation
    }.compile()

    private fun createKotlinFile(
        @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
    ): SourceFile {
        return SourceFile.kotlin("testfile.kt", KOTLIN_TEST_FILE_PREFIX + code + KOTLIN_TEST_FILE_SUFFIX)
    }

    protected fun KotlinCompilation.Result.assertCompilationError() {
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode)
    }

    protected fun KotlinCompilation.Result.assertCompilationError(errorMessage: String) {
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode)
        assertTrue(errorMessage in messages)
    }

    protected fun KotlinCompilation.Result.assertKspError(message: String) {
        val messageExists = messages.splitToSequence(newline)
            .any { it.startsWith("$KSP_ERROR_PREFIX$message") }
        assertTrue(messageExists)
    }

    protected fun KotlinCompilation.Result.assertOk() {
        assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
    }

    protected fun KotlinCompilation.Result.assertExpectedMessage(expectedMessage: String) {
        assertTrue("Expected message containing text '$expectedMessage' but got: '$messages'") {
            messages.contains(expectedMessage)
        }
    }

    protected fun KotlinCompilation.Result.assertExpectedLineNumber(lineNumber: Int) {
        assertExpectedMessage("testfile.kt:$lineNumber")
    }

    protected fun compileAndCheckAndRun(
        @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
        vararg cases: TestCase,
        onCompilationResult: KotlinCompilation.Result.() -> Unit = { assertOk() },
    ) {
        val kotlinSource = createKotlinFile(code)
        val compilationResult = compile(kspWithCompilation = true, kotlinSource, emptyJavaSourceFile())
        compilationResult.onCompilationResult()
        cases.forEach {
            validateCase(compilationResult, it)
        }
    }

    private fun emptyJavaSourceFile() = SourceFile.java("Empty.java", "public class Empty {}")

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
        val projectRoot = "../../../"
        val mainKtsLib = "${projectRoot}frontend/ksp/testing/lib/kotlin-main-kts-1.9.0.jar"
        val runtimeClassFiles = "${projectRoot}runtime/build/classes/kotlin/main"
        val annotationClassFiles = "${projectRoot}annotation/build/classes/kotlin/main"
        val classFiles = "${projectRoot}frontend/ksp/processor/build/ksptesting/classes"
        val classpath = "$mainKtsLib:$annotationClassFiles:$runtimeClassFiles:$classFiles"
        val file = File.createTempFile("sample", ".main.kts").apply {
            writeText(KOTLIN_TEST_FILE_PREFIX + sampleScript + KOTLIN_TEST_FILE_SUFFIX)
        }
        val process = ProcessBuilder("kotlin", "-cp", classpath, file.absolutePath).start()
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

    private companion object {
        private const val KSP_ERROR_PREFIX = "e: [ksp] "
        private val newline = Regex("\n")
        private val logger = InterceptingLogging.logger {}
    }
}

class TestCase(
    @Language("kotlin", prefix = KOTLIN_TEST_SCRIPT_FILE_PREFIX, suffix = KOTLIN_TEST_SCRIPT_FILE_SUFFIX)
    val sampleScript: String,
    val expectedResult: ScriptResult,
) {

    sealed class ScriptResult {

        class Termination(val string: String) : ScriptResult()

        sealed class Error : ScriptResult() {
            abstract val message: String

            class Exception(override val message: String) : Error()
            class KspError(override val message: String) : Error()
        }
    }
}


fun sample(
    @Language("kotlin", prefix = KOTLIN_TEST_SCRIPT_FILE_PREFIX, suffix = KOTLIN_TEST_SCRIPT_FILE_SUFFIX)
    sample: String
) = sample

@Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX)
operator fun String.minus(result: TestCase.ScriptResult) = TestCase(this.trimIndent(), result)

        private const val KOTLIN_TEST_FILE_PREFIX =
            """
            package tests
            
            import io.kabu.annotation.Pattern 
            import io.kabu.annotation.LocalPattern 
            import io.kabu.annotation.ContextCreator
            import io.kabu.annotation.Context
            // lines below are for future imports or other declarations to fix line numbers used in tests            
            
            
            
            
            // end of common part
            """

        private const val KOTLIN_TEST_FILE_SUFFIX = ""

private const val KOTLIN_TEST_SCRIPT_FILE_PREFIX =
    """
            package tests

            import io.kabu.annotation.Pattern
            import io.kabu.annotation.LocalPattern
            import io.kabu.annotation.ContextCreator
            // lines below are for future imports or other declarations to fix line numbers used in tests




            // end of common part
            run {
            """
private const val KOTLIN_TEST_SCRIPT_FILE_SUFFIX = "" +
    "}"
